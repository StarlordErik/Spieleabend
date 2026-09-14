package de.kaserik.impulse.game

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.kaserik.impulse.common.GAME_ID_ARG
import de.kaserik.impulse.common.PreferenceKeys
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.data.GameRepositoryImpl
import de.kaserik.impulse.data.ImpulseDatabase
import de.kaserik.impulse.data.SharedPreferencesAppSettingsRepository
import de.kaserik.impulse.domain.usecase.DrawNextCardUseCase
import de.kaserik.impulse.domain.usecase.DrawNextRandomCardUseCase
import de.kaserik.impulse.domain.usecase.GetOrDrawInitialCardUseCase
import de.kaserik.impulse.domain.usecase.ResetAllCardsForGameUseCase
import de.kaserik.impulse.domain.usecase.ResetSeenCardsUseCase
import de.kaserik.impulse.domain.usecase.ResetTextsPerCardUseCase
import de.kaserik.impulse.domain.usecase.SetCardTextPlayedStateUseCase
import de.kaserik.impulse.domain.usecase.SetTextsPerCardUseCase
import de.kaserik.impulse.domain.usecase.ShowPreviousCardUseCase
import de.kaserik.impulse.domain.usecase.UpdateCardTextSettingsUseCase
import de.kaserik.impulse.frontend.game.GameScreenUiState
import de.kaserik.impulse.frontend.game.CardSwipeTarget
import de.kaserik.impulse.frontend.game.GameUiState
import de.kaserik.impulse.frontend.game.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameFlowIntegrationTest {
    private val context = object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
            super.getSharedPreferences("${name}_game_flow_test", mode)
    }
    private lateinit var database: ImpulseDatabase
    private lateinit var repository: GameRepositoryImpl
    private lateinit var settings: SharedPreferencesAppSettingsRepository
    private val viewModelStore = ViewModelStore()

    @Before
    fun setUp() {
        context.getSharedPreferences(PreferenceKeys.PREFERENCES_NAME, Context.MODE_PRIVATE).edit { clear() }
        settings = SharedPreferencesAppSettingsRepository(context)
        database = Room.inMemoryDatabaseBuilder(context, ImpulseDatabase::class.java).build()
        repository = GameRepositoryImpl(database)
        seedDatabase()
    }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) { viewModelStore.clear() }
        database.close()
    }

    @Test
    fun manualPlaySavesThePlayedTextAndDrawsOnceFromTheLastCategory() = runBlocking {
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        withContext(Dispatchers.Main) { viewModel.selectKategorie(11) }
        val selected = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }
        val playedId = selected.aktuelleKarte.kartentexte.single().id
        val historySize = database.kartenverlaufDao().neuesteKarten(1, 10).size

        withContext(Dispatchers.Main) {
            viewModel.setKartentextManuellGespielt(playedId, gespielt = true)
            viewModel.setKartentextManuellGespielt(playedId, gespielt = true)
        }
        val next = viewModel.awaitGame { it.aktuelleKarte.instanceId != selected.aktuelleKarte.instanceId }

        assertEquals(11, next.aktuelleKarte.kartentexte.single().kategorieId)
        assertTrue(next.aktuelleKarte.kartentexte.single().id != playedId)
        assertTrue(requireNotNull(database.kartentextDao().kartentext(playedId)).gespielt)
        assertEquals(historySize + 1, database.kartenverlaufDao().neuesteKarten(1, 10).size)
        assertEquals(11, settings.getLastDrawCategoryId(1))
        assertEquals(11, next.lastDrawCategoryId)
        assertEquals(selected.aktuelleKarte.instanceId, next.previousCard?.instanceId)
    }

    @Test
    fun automaticMarkingKeepsTheCurrentCard() = runBlocking {
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        val cardTextId = initial.aktuelleKarte.kartentexte.single().id

        withContext(Dispatchers.Main) { viewModel.setKartentextGespielt(cardTextId, gespielt = true) }
        val marked = viewModel.awaitGame { it.aktuelleKarte.kartentexte.single().gespielt }

        assertEquals(initial.aktuelleKarte.instanceId, marked.aktuelleKarte.instanceId)
        assertEquals(initial.aktuelleKarte.instanceId, repository.getCurrentCard(1)?.instanceId)
        assertTrue(requireNotNull(database.kartentextDao().kartentext(cardTextId)).gespielt)
    }

    @Test
    fun randomDrawRemainsRandomAfterReopeningInsteadOfUsingTheDisplayedCategory() = runBlocking {
        var viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        withContext(Dispatchers.Main) { viewModel.selectKategorie(11) }
        val selected = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }
        withContext(Dispatchers.Main) { viewModel.selectRandom() }
        val randomCard = viewModel.awaitGame { it.aktuelleKarte.instanceId != selected.aktuelleKarte.instanceId }
        val current = randomCard.aktuelleKarte.kartentexte.single()
        assertNull(settings.getLastDrawCategoryId(1))

        val otherCategory = if (current.kategorieId == 11) 12 else 11
        val availableId = if (otherCategory == 11) 101 else 103
        repository.setCardTextsPlayedState(setOf(101, 102, 103, 104) - current.id - availableId, gespielt = true)
        withContext(Dispatchers.Main) { viewModelStore.clear() }
        viewModel = createViewModel()
        viewModel.awaitGame()
        withContext(Dispatchers.Main) { viewModel.setKartentextManuellGespielt(current.id, gespielt = true) }
        val next = viewModel.awaitGame { it.aktuelleKarte.instanceId != randomCard.aktuelleKarte.instanceId }

        assertEquals(availableId, next.aktuelleKarte.kartentexte.single().id)
        assertEquals(otherCategory, next.aktuelleKarte.kartentexte.single().kategorieId)
        assertNull(next.lastDrawCategoryId)
    }

    @Test
    fun previousPreviewPreservesHistoryUntilTheSwipeIsCommitted() = runBlocking {
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        assertNull(initial.previousCard)
        withContext(Dispatchers.Main) { viewModel.selectKategorie(11) }
        val selected = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }

        assertEquals(initial.aktuelleKarte.instanceId, selected.previousCard?.instanceId)
        repeat(2) { assertEquals(initial.aktuelleKarte.instanceId, repository.getPreviousCard(1)?.instanceId) }
        assertEquals(selected.aktuelleKarte.instanceId, repository.getCurrentCard(1)?.instanceId)
        assertEquals(2, database.kartenverlaufDao().neuesteKarten(1, 10).size)

        withContext(Dispatchers.Main) { viewModel.selectPrevious() }
        val previous = viewModel.awaitGame { it.aktuelleKarte.instanceId == initial.aktuelleKarte.instanceId }
        assertFalse(previous.hasPreviousCard)
        assertNull(previous.previousCard)
        assertEquals(1, database.kartenverlaufDao().neuesteKarten(1, 10).size)
    }

    @Test
    fun idlePrefetchAndCancelledSwipesKeepUnshownTextsUnseen() = runBlocking {
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        val before = repository.getGame(1)
        withContext(Dispatchers.Main) { viewModel.cardPreloader.setActive(true) }
        delay(500)
        val prepared = withContext(Dispatchers.Main) {
            requireNotNull(viewModel.prepareCardSwipe(CardSwipeTarget.Category(11)))
        }
        withContext(Dispatchers.Main) { viewModel.prepareCardSwipe(CardSwipeTarget.Random) }

        assertEquals(before, repository.getGame(1))
        assertEquals(initial.aktuelleKarte.instanceId, repository.getCurrentCard(1)?.instanceId)
        assertEquals(1, database.kartenverlaufDao().neuesteKarten(1, 10).size)

        withContext(Dispatchers.Main) { prepared.commit() }
        val next = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }
        assertEquals(prepared.card.kartentexte, next.aktuelleKarte.kartentexte)
        val seenIds = repository.getGame(1).kategorien.flatMap { it.kartentexte }
            .filter { it.gesehen }.map { it.id() }.toSet()
        assertEquals(
            (initial.aktuelleKarte.kartentexte + prepared.card.kartentexte).map { it.id }.toSet(),
            seenIds,
        )
    }

    @Test
    fun changingTextCountDiscardsASwipePreparedForTheSameCurrentCard() = runBlocking {
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        val unusedCategory = initial.kategorien.single {
            it.id != initial.aktuelleKarte.kartentexte.single().kategorieId
        }.id
        val prepared = withContext(Dispatchers.Main) {
            requireNotNull(viewModel.prepareCardSwipe(CardSwipeTarget.Category(unusedCategory)))
        }
        assertEquals(1, prepared.card.kartentexte.size)

        withContext(Dispatchers.Main) {
            viewModel.setTextsPerCard(2)
            prepared.commit()
        }
        val next = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }
        assertEquals(2, next.aktuelleKarte.kartentexte.size)
        assertEquals(2, next.texteProKarte)
        assertEquals(2, database.kartenverlaufDao().neuesteKarten(1, 10).size)
    }

    @Test
    fun addingAMissingTranslationRefreshesTheCurrentCardWithoutDrawingAgain() = runBlocking {
        settings.setLanguage(Sprache.EN)
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame { it.sprache == Sprache.EN }
        withContext(Dispatchers.Main) { viewModel.selectKategorie(11) }
        val current = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }
        val text = current.aktuelleKarte.kartentexte.single()
        assertTrue(text.uebersetzungFehlt)
        withContext(Dispatchers.Main) { viewModel.setEigeneKartentextLokalisierung(text.id, "Added translation") }

        val translated = viewModel.awaitGame { it.aktuelleKarte.kartentexte.single().text == "Added translation" }
        assertFalse(translated.aktuelleKarte.kartentexte.single().uebersetzungFehlt)
        assertEquals(current.aktuelleKarte.instanceId, translated.aktuelleKarte.instanceId)
        assertEquals("Added translation", ownText(text.id, Sprache.EIGENE_EN))
        assertEquals(2, database.kartenverlaufDao().neuesteKarten(1, 10).size)
    }

    @Test
    fun categorySelectionIsStoredPerGameAndSurvivesReopening() = runBlocking {
        settings.setLastDrawCategoryId(1, 11)
        settings.setLastDrawCategoryId(2, 21)
        val viewModel = createViewModel()
        val initial = viewModel.awaitGame()
        withContext(Dispatchers.Main) { viewModel.selectNextFromLastCategory() }
        val next = viewModel.awaitGame { it.aktuelleKarte.instanceId != initial.aktuelleKarte.instanceId }

        assertEquals(11, next.aktuelleKarte.kartentexte.single().kategorieId)
        assertEquals(21, settings.getLastDrawCategoryId(2))
        assertEquals(11, SharedPreferencesAppSettingsRepository(context).getLastDrawCategoryId(1))
    }

    @Test
    fun missingTranslationsAreStoredInTheSelectedOwnLanguageAndCanBeReset() = runBlocking {
        val cases = listOf(101 to Sprache.EN, 104 to Sprache.DE)
        cases.forEach { (id, language) ->
            val before = repository.getGame(1).kategorien.flatMap { it.originaleKartentexte }.single { it.id() == id }
            assertTrue(before.lokalisierung.lokalisierterText(language).uebersetzungFehlt)
            repository.setCustomCardTextTranslation(id, language, "Eigene Übersetzung")
            val after = repository.getGame(1).kategorien.flatMap { it.originaleKartentexte }.single { it.id() == id }

            assertEquals("Eigene Übersetzung", after.text(language))
            assertFalse(after.lokalisierung.lokalisierterText(language).uebersetzungFehlt)
            assertTrue(after.lokalisierung.hatEigeneLokalisierungFuer(language))
            assertTrue(after.lokalisierung.translationen.single { it.sprache == language.eigeneSprache() }.bearbeitet)
            assertEquals(before.text(Sprache.OG), after.text(Sprache.OG))
        }
        repository.resetCustomCardTextTranslations(gameId = 1)
        assertTrue(repository.getGame(1).kategorien.flatMap { it.originaleKartentexte }
            .none { it.lokalisierung.hatEigeneLokalisierung() })
    }

    @Test
    fun erikImportAndResetRespectScopeAndExistingOwnTranslationsInRoom() = runBlocking {
        repository.setCustomCardTextTranslation(101, Sprache.DE, "Mein Text")
        repository.setCustomCardTextTranslation(101, Sprache.EN, "My text")
        repository.applyErikCardTextTranslations(gameId = 1, overwriteExisting = false)
        assertEquals("Mein Text", ownText(101, Sprache.EIGENE_DE))
        assertEquals("Erik 103", ownText(103, Sprache.EIGENE_DE))
        assertTrue(database.lokalisierungDao().translationenFuerLokalisierung(201).none {
            it.sprache == Sprache.EIGENE_DE
        })

        repository.applyErikCardTextTranslations(gameId = 1, overwriteExisting = true)
        assertEquals("Erik 101", ownText(101, Sprache.EIGENE_DE))
        assertEquals("My text", ownText(101, Sprache.EIGENE_EN))
        repository.applyErikCardTextTranslations(gameId = null, overwriteExisting = false)
        repository.resetCustomCardTextTranslations(gameId = 1)
        assertEquals("Erik 201", ownText(201, Sprache.EIGENE_DE))
        repository.resetCustomCardTextTranslations(gameId = null)
        assertTrue(database.lokalisierungDao().translationenFuerLokalisierung(201).none {
            it.sprache == Sprache.EIGENE_DE
        })
        assertEquals("Erik 101", ownText(101, Sprache.ERIK))
    }

    private suspend fun ownText(id: Int, language: Sprache): String =
        database.lokalisierungDao().translationenFuerLokalisierung(id).single { it.sprache == language }.text

    private suspend fun createViewModel(): GameViewModel = withContext(Dispatchers.Main) {
        val drawRandom = DrawNextRandomCardUseCase(repository)
        GameViewModel(
            savedStateHandle = SavedStateHandle(mapOf(GAME_ID_ARG to "1")),
            drawNextCard = DrawNextCardUseCase(repository),
            getOrDrawInitialCard = GetOrDrawInitialCardUseCase(repository, drawRandom),
            showPreviousCard = ShowPreviousCardUseCase(repository),
            setCardTextPlayedState = SetCardTextPlayedStateUseCase(repository),
            resetSeenCardsUseCase = ResetSeenCardsUseCase(repository),
            resetAllCardsUseCase = ResetAllCardsForGameUseCase(repository),
            setTextsPerCardUseCase = SetTextsPerCardUseCase(repository),
            resetTextsPerCardUseCase = ResetTextsPerCardUseCase(repository),
            updateCardTextSettings = UpdateCardTextSettingsUseCase(repository),
            appSettingsRepository = settings,
        ).also { viewModelStore.put("game", it) }
    }

    private suspend fun GameViewModel.awaitGame(predicate: (GameUiState) -> Boolean = { true }): GameUiState =
        withTimeout(10_000L) { uiState.filterIsInstance<GameScreenUiState.Loaded>().first { predicate(it.game) }.game }

    private fun seedDatabase() {
        val db = database.openHelper.writableDatabase
        listOf(1, 2, 11, 12, 21, 101, 102, 103, 104, 201).forEach { id ->
            val language = if (id == 104) "EN" else "DE"
            db.execSQL("INSERT INTO lokalisierung (id, og_sprache) VALUES (?, ?)", arrayOf<Any>(id, language))
            db.execSQL("INSERT INTO translation VALUES (?, 'OG', ?, 0)", arrayOf<Any>(id, "Original $id"))
        }
        listOf(1, 2).forEach { id -> db.execSQL("INSERT INTO spiel VALUES (?, 0, 0, 0, NULL, 1)", arrayOf(id)) }
        listOf(11, 12, 21).forEach { id -> db.execSQL("INSERT INTO kategorie VALUES (?, 0, 0, 0)", arrayOf(id)) }
        listOf(101, 102, 103, 104, 201).forEach { id ->
            db.execSQL("INSERT INTO kartentext VALUES (?, 0, 0, 0, 0, 0)", arrayOf(id))
        }
        db.execSQL("INSERT INTO spiel_x_kategorie VALUES (1, 11, 0, 0), (1, 12, 0, 0), (2, 21, 0, 0)")
        db.execSQL("INSERT INTO kategorie_x_kartentext VALUES " +
            "(11, 101, 0, 0), (11, 102, 0, 0), (12, 103, 0, 0), (12, 104, 0, 0), (21, 201, 0, 0)")
        listOf(101, 103, 201).forEach { id ->
            db.execSQL("INSERT INTO translation VALUES (?, 'ERIK', ?, 0)", arrayOf<Any>(id, "Erik $id"))
        }
    }
}
