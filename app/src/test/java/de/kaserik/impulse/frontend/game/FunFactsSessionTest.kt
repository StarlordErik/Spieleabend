package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FunFactsSessionTest {
    @Test
    fun selectedQuestionIsCompletedOnlyAfterTheLastSignAndOnlyOnce() {
        val completedQuestions = mutableListOf<Int>()
        val session = FunFactsSession(onRoundCompleted = { completedQuestions.add(it) })
        session.configurePlayerCount(2)
        session.selectQuestion(9)
        session.reopenQuestionSelection()
        session.selectQuestion(9)
        session.addPlayer("Ada", "10")
        session.restartGame()
        assertTrue(completedQuestions.isEmpty())

        session.configurePlayerCount(2)
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "90")
        session.beginReveal()
        session.beginReveal()
        assertEquals(FunFactsPhase.Revealing, session.phase)
        assertTrue(completedQuestions.isEmpty())

        session.beginReveal()
        assertEquals(listOf(10), completedQuestions)
        session.beginReveal()
        session.toggleRevealedSide(session.players.first().id)
        session.startNextRound()
        session.restartGame()
        assertEquals(listOf(10), completedQuestions)
    }

    @Test
    fun restoredRoundStillReportsCompletionWhenItsLastSignIsRevealed() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "90")
        session.beginReveal()
        session.beginReveal()
        val completedQuestions = mutableListOf<Int>()
        val restored = FunFactsSession.restore(
            session.serialize(), onRoundCompleted = { completedQuestions.add(it) },
        )
        assertTrue(completedQuestions.isEmpty())

        restored.beginReveal()
        assertEquals(listOf(10), completedQuestions)
        FunFactsSession.restore(
            restored.serialize(), onRoundCompleted = { completedQuestions.add(it) },
        ).beginReveal()
        assertEquals(listOf(10), completedQuestions)
    }

    @Test
    fun questionCanOnlyBeChangedBeforeFirstSignIsFinished() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10, QUESTION_ORIGIN)

        assertEquals(10, session.reopenQuestionSelection())
        assertTrue(session.selectingQuestion)
        assertNull(session.selectedQuestionOrigin)

        session.selectQuestion(11)
        session.addPlayer("Ada", "42")

        assertNull(session.reopenQuestionSelection())
        assertEquals(11, session.selectedQuestionId)
    }

    @Test
    fun activeSignCanBeInsertedBetweenOtherSigns() {
        val session = FunFactsSession().apply { configurePlayerCount(3) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "90")
        session.nextPlayer()
        session.addPlayer("Cy", "50")

        session.moveActiveSign(-1)

        assertEquals(
            listOf(nameMarker("Ada"), nameMarker("Cy"), nameMarker("Bo")),
            session.players.map { it.name.strokes.first().last().x },
        )
    }

    @Test
    fun signsRevealFromBottomToTop() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "90")

        session.beginReveal()
        assertEquals(FunFactsPhase.FinalPositioning, session.phase)
        session.beginReveal()

        assertFalse(session.players.first().revealed)
        assertTrue(session.players.last().revealed)
        session.beginReveal()
        assertEquals(FunFactsPhase.Complete, session.phase)
    }

    @Test
    fun revealedAnswerCanBeHiddenWithoutLosingRevealOrder() {
        val session = FunFactsSession().apply { configurePlayerCount(3) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "50")
        session.nextPlayer()
        session.addPlayer("Cy", "90")

        session.beginReveal()
        session.beginReveal()
        val bottomPlayer = session.players.last()
        session.toggleRevealedSide(bottomPlayer.id)
        session.toggleRevealedSide(bottomPlayer.id)
        session.toggleRevealedSide(bottomPlayer.id)
        val restored = FunFactsSession.restore(session.serialize())

        assertTrue(restored.players.last().revealed)
        assertFalse(restored.players.last().answerVisible)

        restored.beginReveal()

        assertTrue(restored.players.last().answerVisible)
        assertTrue(restored.players[1].revealed)
        assertTrue(restored.players[1].answerVisible)
        assertFalse(restored.players.first().revealed)
    }

    @Test
    fun strokeWidthsArePreservedPerStroke() {
        val draft = FunFactsDraftDrawing()
        draft.startStroke(Offset.Zero, DEFAULT_DRAWING_STROKE_WIDTH_FRACTION)
        draft.continueStroke(Offset(0.2f, 0.2f))
        draft.startStroke(Offset(0.3f, 0.3f), DEFAULT_DRAWING_STROKE_WIDTH_FRACTION * 2f)

        assertEquals(
            listOf(
                DEFAULT_DRAWING_STROKE_WIDTH_FRACTION,
                DEFAULT_DRAWING_STROKE_WIDTH_FRACTION * 2f,
            ),
            draft.snapshot().strokeWidthFractions,
        )
    }

    @Test
    fun newPlayersStartWithMiddleWidthAndKnownPlayersRestoreTheirLastSelection() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)

        assertEquals(DEFAULT_STROKE_WIDTH_INDEX, session.selectedStrokeWidthIndex)
        session.selectStrokeWidth(2)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        assertEquals(DEFAULT_STROKE_WIDTH_INDEX, session.selectedStrokeWidthIndex)
        session.selectStrokeWidth(0)
        session.addPlayer("Bo", "20")

        session.startNextRound()
        val restored = FunFactsSession.restore(session.serialize())
        restored.selectQuestion(11)

        assertEquals(0, restored.selectedStrokeWidthIndex)
        restored.addPlayer("Bo", "30")
        restored.nextPlayer()
        assertEquals(2, restored.selectedStrokeWidthIndex)
    }

    @Test
    fun allRevealedSignsCanStillBeFlippedRepeatedly() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "50")
        session.beginReveal()
        session.beginReveal()
        session.beginReveal()

        assertEquals(FunFactsPhase.Complete, session.phase)
        val topPlayerId = session.players.first().id
        session.toggleRevealedSide(topPlayerId)
        assertFalse(session.players.first().answerVisible)
        session.toggleRevealedSide(topPlayerId)
        assertTrue(session.players.first().answerVisible)
    }

    @Test
    fun selectedStrokeWidthSurvivesSessionSerialization() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        val wideStroke = DEFAULT_DRAWING_STROKE_WIDTH_FRACTION * 2f
        session.draftName.startStroke(Offset.Zero, wideStroke)
        session.draftAnswer.startStroke(Offset.Zero)
        session.finishAnswer()

        val restored = FunFactsSession.restore(session.serialize())

        assertEquals(wideStroke, restored.players.single().name.strokeWidthFractions.single())
    }

    @Test
    fun selectedQuestionOriginSurvivesSessionSerialization() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(71, QUESTION_ORIGIN)

        val restored = FunFactsSession.restore(session.serialize())

        assertEquals(71, restored.selectedQuestionId)
        assertEquals(QUESTION_ORIGIN, restored.selectedQuestionOrigin)
    }

    @Test
    fun versionSixSessionWithoutQuestionOriginCanStillBeRestored() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(71, QUESTION_ORIGIN)
        val versionSixLines = session.serialize().lines().toMutableList().apply {
            this[0] = "6"
            removeAt(lastIndex)
            removeAt(4)
        }

        val restored = FunFactsSession.restore(versionSixLines.joinToString("\n"))

        assertEquals(71, restored.selectedQuestionId)
        assertNull(restored.selectedQuestionOrigin)
    }

    @Test
    fun restartReturnsToQuestionSelectionAndClearsAllSigns() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.selectColor(3)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.draftAnswer.startStroke(Offset.Zero)

        session.restartGame()

        assertTrue(session.selectingQuestion)
        assertNull(session.selectedQuestionId)
        assertTrue(session.players.isEmpty())
        assertTrue(session.draftName.strokes.isEmpty())
        assertTrue(session.draftAnswer.strokes.isEmpty())
        assertEquals((0 until 10).toList(), session.availableColorIndices)

        assertTrue(session.needsPlayerCount)
        session.configurePlayerCount(2)
        session.selectQuestion(11)
        assertTrue(session.draftName.strokes.isEmpty())
        assertEquals(DEFAULT_STROKE_WIDTH_INDEX, session.selectedStrokeWidthIndex)
    }

    @Test
    fun nextRoundStartsWithSecondPlayerFromPreviousRound() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.addPlayer("Bo", "90")
        session.moveActiveSign(-1)

        session.startNextRound()
        session.selectQuestion(12)

        assertEquals(nameMarker("Bo"), session.draftName.strokes.first().last().x)
    }

    @Test
    fun assignedSignColorsCannotBeSelectedAgain() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.selectColor(3)
        session.addPlayer("Ada", "10")
        session.nextPlayer()

        assertFalse(3 in session.availableColorIndices)
        assertTrue(session.selectedColorIndex in session.availableColorIndices)

        val automaticallySelectedColor = session.selectedColorIndex
        session.selectColor(3)
        assertEquals(automaticallySelectedColor, session.selectedColorIndex)
        session.addPlayer("Bo", "20")
        assertEquals(2, session.players.size)
        assertEquals(2, session.players.map { it.colorIndex }.distinct().size)
    }

    @Test
    fun playersKeepTheirReservedColorsInTheNextRound() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.selectColor(3)
        session.addPlayer("Ada", "10")
        session.nextPlayer()
        session.selectColor(7)
        session.addPlayer("Bo", "20")

        session.startNextRound()
        session.selectQuestion(11)

        assertEquals(7, session.selectedColorIndex)
        assertFalse(3 in session.availableColorIndices)
        session.addPlayer("Bo", "30")
        session.nextPlayer()
        assertEquals(3, session.selectedColorIndex)
        assertFalse(7 in session.availableColorIndices)
    }

    @Test
    fun reservedColorsSurviveSessionSerialization() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.selectColor(4)
        session.addPlayer("Ada", "10")
        session.startNextRound()

        val restored = FunFactsSession.restore(session.serialize())
        restored.selectQuestion(11)

        assertEquals(4, restored.selectedColorIndex)
    }

    @Test
    fun finishedDrawingIsIndependentFromNextPlayersCanvas() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        val savedDrawing = session.players.single().answer
        session.nextPlayer()
        session.draftAnswer.startStroke(Offset(0.9f, 0.9f))

        assertEquals(1, savedDrawing.strokes.size)
        assertEquals(2, savedDrawing.strokes.single().size)
    }

    @Test
    fun serializedSessionRestoresRoundAndDrawings() {
        val session = FunFactsSession().apply { configurePlayerCount(2) }
        session.selectQuestion(71)
        session.selectColor(4)
        session.addPlayer("Ada", "42")

        val restored = FunFactsSession.restore(session.serialize())

        assertEquals(FunFactsPhase.PositionSign, restored.phase)
        assertEquals(71, restored.selectedQuestionId)
        assertEquals(4, restored.players.single().colorIndex)
        assertEquals(session.players.single().name, restored.players.single().name)
        assertEquals(session.players.single().answer, restored.players.single().answer)
    }


    @Test
    fun playerCountIsRequiredAndPersistsUntilRestart() {
        val session = FunFactsSession()
        session.selectQuestion(10)
        assertNull(session.selectedQuestionId)
        listOf(-1, 0, 1, 11).forEach(session::configurePlayerCount)
        assertTrue(session.needsPlayerCount)
        session.configurePlayerCount(5)
        session.configurePlayerCount(3)
        val restored = FunFactsSession.restore(session.serialize())
        assertEquals(5, restored.totalPlayerCount)
        restored.selectQuestion(10)
        restored.addPlayer("Ada", "10")
        restored.startNextRound()
        assertEquals(5, restored.totalPlayerCount)
        restored.restartGame()
        assertTrue(restored.needsPlayerCount)
    }

    @Test
    fun revealRequiresEveryConfiguredPlayerAndNoExtraPlayerCanJoin() {
        for (count in MIN_GAME_PLAYERS..MAX_GAME_PLAYERS) {
            val session = FunFactsSession().apply { configurePlayerCount(count) }
            session.selectQuestion(10)
            repeat(count) { index ->
                session.addPlayer("Person $index", "10")
                if (index < count - 1) {
                    assertTrue(session.canAddPlayer)
                    assertFalse(session.canBeginReveal)
                    session.beginReveal()
                    assertEquals(FunFactsPhase.PositionSign, session.phase)
                    session.nextPlayer()
                }
            }
            val restored = FunFactsSession.restore(session.serialize())
            assertFalse(restored.canAddPlayer)
            assertTrue(restored.canBeginReveal)
            restored.nextPlayer()
            assertEquals(FunFactsPhase.PositionSign, restored.phase)
            restored.beginReveal()
            assertEquals(FunFactsPhase.FinalPositioning, restored.phase)
            assertEquals(count, restored.players.size)
        }
    }

    @Test
    fun olderPartialRoundsAskForCountWithoutLosingSigns() {
        val session = FunFactsSession().apply { configurePlayerCount(4) }
        session.selectQuestion(10)
        session.addPlayer("Ada", "10")
        val legacy = session.serialize().substringBeforeLast("\n")
        val restored = FunFactsSession.restore(legacy)
        assertTrue(restored.needsPlayerCount)
        assertEquals(session.players.toList(), restored.players.toList())
        restored.configurePlayerCount(4)
        restored.nextPlayer()
        assertEquals(FunFactsPhase.EnterAnswer, restored.phase)
    }

    private fun FunFactsSession.addPlayer(name: String, answer: String) {
        draftName.startStroke(Offset.Zero)
        draftName.continueStroke(Offset(nameMarker(name), nameMarker(name)))
        draftAnswer.startStroke(Offset.Zero)
        draftAnswer.continueStroke(Offset(answer.length / 100f, answer.length / 100f))
        finishAnswer()
    }

    private fun nameMarker(name: String): Float = name.length / 100f

    private companion object {
        val QUESTION_ORIGIN = FunFactsQuestionOrigin(
            leftFraction = 0.1f,
            topFraction = 0.3f,
            widthFraction = 0.8f,
            heightFraction = 0.2f,
        )
    }
}
