package de.kaserik.impulse.frontend.game

import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacySessionTest {
    @Test
    fun selectedQuestionIsCompletedOnlyAfterEvaluationAndOnlyOnce() {
        val completedQuestions = mutableListOf<Int>()
        val session = PrivacySession(onRoundCompleted = { completedQuestions.add(it) })
        session.configurePlayerCount(2)
        session.selectQuestion(QUESTION_ID, CARD_ID)
        session.reopenQuestionSelection()
        session.selectQuestion(QUESTION_ID, CARD_ID)
        session.enter("Alex", true, 1)
        session.nextPlayer()
        session.restartGame()
        assertTrue(completedQuestions.isEmpty())

        session.configurePlayerCount(2)
        session.selectQuestion(QUESTION_ID, CARD_ID)
        session.enter("Alex", true, 1)
        session.nextPlayer()
        session.enter("Sam", false, 1)
        session.reveal()
        assertEquals(PrivacyPhase.AwaitingReveal, session.phase)
        session.completeReveal()
        session.startNextRound()
        assertEquals(PrivacyPhase.AwaitingReveal, session.phase)
        assertTrue(session.ranking.all { it.player.points == 0 })
        assertTrue(completedQuestions.isEmpty())

        session.startReveal()
        session.startReveal()
        assertEquals(PrivacyPhase.Revealing, session.phase)
        assertTrue(completedQuestions.isEmpty())

        session.completeReveal()
        assertEquals(listOf(QUESTION_ID), completedQuestions)
        session.completeReveal()
        session.startNextRound()
        session.restartGame()
        assertEquals(listOf(QUESTION_ID), completedQuestions)
    }

    @Test
    fun restoredEvaluationStillReportsTheCompletedQuestion() {
        val session = newRound()
        session.enter("Alex", true, 1)
        session.nextPlayer()
        session.enter("Sam", false, 1)
        session.reveal()
        session.startReveal()
        val completedQuestions = mutableListOf<Int>()
        val restored = PrivacySession.restore(
            session.serialize(), onRoundCompleted = { completedQuestions.add(it) },
        )
        assertTrue(completedQuestions.isEmpty())

        restored.completeReveal()
        assertEquals(listOf(QUESTION_ID), completedQuestions)
        PrivacySession.restore(
            restored.serialize(), onRoundCompleted = { completedQuestions.add(it) },
        ).completeReveal()
        assertEquals(listOf(QUESTION_ID), completedQuestions)
    }

    @Test
    fun exactNearAndMissedPredictionsScoreAccordingToRules() {
        assertEquals(3, privacyPoints(4, yesCount = 4, playerCount = 6))
        assertEquals(1, privacyPoints(3, yesCount = 4, playerCount = 6))
        assertEquals(1, privacyPoints(5, yesCount = 4, playerCount = 6))
        assertEquals(0, privacyPoints(2, yesCount = 4, playerCount = 6))
        assertEquals(0, privacyPoints(6, yesCount = 4, playerCount = 6))
        assertEquals(1, privacyPoints(1, yesCount = 0, playerCount = 6))
        assertEquals(0, privacyPoints(2, yesCount = 0, playerCount = 6))
    }

    @Test
    fun unanimousYesCapsBothExactAndNearbyGuessesAtOnePoint() {
        for (playerCount in 1..PRIVACY_MAX_PLAYERS) {
            assertEquals(1, privacyPoints(playerCount, playerCount, playerCount))
            if (playerCount > 1) assertEquals(
                1,
                privacyPoints(playerCount - 1, playerCount, playerCount)
            )
            if (playerCount < PRIVACY_MAX_PLAYERS) assertEquals(
                1,
                privacyPoints(playerCount + 1, playerCount, playerCount)
            )
        }
        assertEquals(0, privacyPoints(8, yesCount = 10, playerCount = 10))
    }

    @Test
    fun emptyNamesAndMissingVotesCannotBeSubmitted() {
        val session = newRound()
        session.nextPlayer()
        session.reveal()
        session.startReveal()
        assertEquals(PrivacyPhase.EnterAnswer, session.phase)
        assertEquals(0, session.playerCount)
        session.draft.updateName("  ")
        session.draft.chooseVote(false)
        assertFalse(session.canSubmit)
        session.draft.updateName("Alex")
        assertTrue(session.canSubmit)
        session.draft.choosePrediction(0)
        assertEquals(1, session.draftPrediction)
        session.draft.choosePrediction(11)
        assertEquals(1, session.draftPrediction)
    }

    @Test
    fun nextPersonCannotSeePreviousVoteOrPrediction() {
        val session = newRound(10)
        session.enter("  Alex  ", true, 7)
        session.nextPlayer()
        assertEquals(2, session.playerNumber)
        assertEquals("", session.draftName)
        assertNull(session.draftVote)
        assertEquals(1, session.draftPrediction)
        assertFalse(session.canSubmit)
        assertFalse(session.canChangeQuestion)
        assertNull(session.reopenQuestionSelection())
    }

    @Test
    fun firstPersonCanChangeQuestionWithoutLosingTheirName() {
        val session = newRound(5)
        session.enter("Alex", false, 4)
        assertEquals(QUESTION_ID, session.reopenQuestionSelection())
        assertTrue(session.selectingQuestion)
        assertEquals("Alex", session.draftName)
        assertNull(session.draftVote)
        assertEquals(1, session.draftPrediction)
        session.selectQuestion(QUESTION_ID + 1, CARD_ID)
        assertEquals("Alex", session.draftName)
        assertEquals(QUESTION_ID + 1, session.selectedQuestionId)
    }

    @Test
    fun tenthPersonCanEvaluateButCannotAddAnEleventhPerson() {
        val session = newRound(10)
        repeat(9) { index ->
            session.enter("Person $index", index % 2 == 0, 5)
            session.nextPlayer()
        }
        session.enter("Person 10", false, 5)
        assertEquals(10, session.playerNumber)
        assertTrue(session.canSubmit)
        assertFalse(session.canGoToNextPlayer)
        session.nextPlayer()
        assertEquals(9, session.playerCount)
        session.reveal()
        session.reveal()
        assertEquals(10, session.playerCount)
        assertEquals(5, session.yesCount)
        session.startReveal()
        session.completeReveal()
        assertEquals(10, session.ranking.size)
        assertTrue(session.ranking.all { it.player.points == 3 && it.rank == 1 })
    }

    @Test
    fun scoreIsAwardedOnceAndOnlyAfterTheReveal() {
        val session = completedRound()
        val saved = session.serialize()
        session.completeReveal()
        session.reveal()
        session.startReveal()
        assertEquals(saved, session.serialize())
        assertEquals(listOf("Alex", "Sam", "Chris"), session.ranking.map { it.player.name })
        assertEquals(listOf(3, 1, 1), session.ranking.map { it.player.points })
    }

    @Test
    fun startPlayerRotatesAndTotalsStayWithPeopleAcrossRounds() {
        val session = completedRound()
        session.startNextRound()
        assertEquals("Sam", session.draftName)
        assertEquals(2, session.roundNumber)
        assertEquals(3, session.totalPlayerCount)
        session.selectQuestion(QUESTION_ID + 1, CARD_ID + 1)
        session.draft.chooseVote(true)
        session.draft.choosePrediction(1)
        session.nextPlayer()
        assertEquals("Chris", session.draftName)
        session.draft.chooseVote(false)
        session.draft.choosePrediction(2)
        session.nextPlayer()
        assertEquals("Alex", session.draftName)
        session.draft.chooseVote(false)
        session.draft.choosePrediction(1)
        session.reveal()
        session.startReveal()
        session.completeReveal()
        assertEquals(listOf("Alex", "Sam", "Chris"), session.ranking.map { it.player.name })
        assertEquals(listOf(6, 4, 2), session.ranking.map { it.player.points })
        session.startNextRound()
        assertEquals("Chris", session.draftName)
    }

    @Test
    fun identicalNamesStillHaveSeparateScoresAndRenamingPreservesIdentity() {
        val session = newRound(3)
        session.enter("Alex", true, 2)
        session.nextPlayer()
        session.enter("Alex", true, 1)
        session.nextPlayer()
        session.enter("Sam", false, 1)
        session.reveal()
        session.startReveal()
        session.completeReveal()
        val secondAlex = session.ranking[1].player
        assertEquals(listOf(3, 1, 1), session.ranking.map { it.player.points })
        session.startNextRound()
        session.selectQuestion(QUESTION_ID + 1, CARD_ID + 1)
        session.enter("Alex 2", false, 1)
        session.nextPlayer()
        session.enter("Sam", false, 2)
        session.nextPlayer()
        session.enter("Alex", false, 2)
        session.reveal()
        session.startReveal()
        session.completeReveal()
        val renamed = session.ranking.first { it.player.id == secondAlex.id }.player
        assertEquals(2, renamed.points)
        assertEquals("Alex 2", renamed.name)
    }

    @Test
    fun sessionRestoresDraftsUnicodeAndEveryRoundPhase() {
        var session = newRound(3)
        session.enter("Zoë | 李 😀", false, 2)
        session = roundTrip(session)
        assertEquals("Zoë | 李 😀", session.draftName)
        assertEquals(false, session.draftVote)
        assertEquals(2, session.draftPrediction)
        session.nextPlayer()
        session.enter("Müller", true, 1)
        session = roundTrip(session)
        assertEquals(1, session.playerCount)
        session.nextPlayer()
        session.enter("Chris", false, 2)
        session.reveal()
        session = roundTrip(session)
        assertEquals(PrivacyPhase.AwaitingReveal, session.phase)
        session.completeReveal()
        assertEquals(PrivacyPhase.AwaitingReveal, session.phase)
        assertTrue(session.ranking.all { it.player.points == 0 })
        session.startReveal()
        session = roundTrip(session)
        assertEquals(PrivacyPhase.Revealing, session.phase)
        assertTrue(session.ranking.all { it.player.points == 0 })
        session.completeReveal()
        session = roundTrip(session)
        assertEquals(3, session.ranking.first().player.points)
        session.startNextRound()
        session = roundTrip(session)
        assertTrue(session.selectingQuestion)
        assertEquals("Müller", session.draftName)
    }

    @Test
    fun malformedOrTruncatedSavedSessionsRecoverToQuestionSelection() {
        listOf(
            null,
            "",
            "not a session",
            completedRound().serialize().dropLast(12)
        ).forEach { serialized ->
            val restored = PrivacySession.restore(serialized)
            assertTrue(restored.selectingQuestion)
            assertEquals(0, restored.playerCount)
            assertEquals("", restored.draftName)
        }
    }

    @Test
    fun changingTheBasicCardCannotLeaveAnOrphanedPrivacyRound() {
        val session = newRound()
        session.enter("Alex", true, 1)
        session.nextPlayer()
        session.onCardChanged(CARD_ID)
        assertFalse(session.selectingQuestion)
        session.onCardChanged(CARD_ID + 1)
        assertTrue(session.selectingQuestion)
        assertEquals(0, session.playerCount)
        assertEquals("Alex", session.draftName)
        assertNull(session.selectedQuestionId)
    }

    @Test
    fun restartClearsNamesPointsAndRoundNumber() {
        val session = completedRound()
        session.startNextRound()
        session.restartGame()
        assertEquals(PrivacySession().serialize(), session.serialize())
    }

    @Test
    fun configuredCountBlocksEarlyEvaluationAndExtraPlayers() {
        for (count in MIN_GAME_PLAYERS..PRIVACY_MAX_PLAYERS) {
            val session = newRound(count)
            repeat(count) { index ->
                session.enter("Person $index", index % 2 == 0, 1)
                assertEquals(index == count - 1, session.canReveal)
                assertEquals(index < count - 1, session.canGoToNextPlayer)
                if (index < count - 1) {
                    session.reveal()
                    session.startReveal()
                    assertEquals(PrivacyPhase.EnterAnswer, session.phase)
                    assertEquals(index, session.playerCount)
                    session.nextPlayer()
                }
            }
            session.nextPlayer()
            assertEquals(count - 1, session.playerCount)
            session.reveal()
            assertEquals(PrivacyPhase.AwaitingReveal, session.phase)
            assertEquals(count, session.playerCount)
            session.startReveal()
            assertEquals(PrivacyPhase.Revealing, session.phase)
        }
    }

    @Test
    fun firstStartRequiresAPlayerCountAndRestartAsksAgain() {
        val session = PrivacySession()
        session.selectQuestion(QUESTION_ID, CARD_ID)
        assertNull(session.selectedQuestionId)
        listOf(-1, 0, 1, 11).forEach(session::configurePlayerCount)
        assertTrue(session.needsPlayerCount)
        session.configurePlayerCount(5)
        session.configurePlayerCount(3)
        val restored = roundTrip(session)
        assertEquals(5, restored.totalPlayerCount)
        assertFalse(restored.needsPlayerCount)
        restored.restartGame()
        assertTrue(restored.needsPlayerCount)
    }

    @Test
    fun predictionsUseOnlyOneThroughPlayerCountMinusOne() {
        for (count in MIN_GAME_PLAYERS..PRIVACY_MAX_PLAYERS) {
            val session = newRound(count)
            session.draft.choosePrediction(Int.MIN_VALUE)
            assertEquals(1, session.draftPrediction)
            session.draft.choosePrediction(Int.MAX_VALUE)
            assertEquals(count - 1, session.draftPrediction)
            assertEquals(count - 1, roundTrip(session).draftPrediction)
        }
    }

    @Test
    fun unanimousVotesAwardAtMostOnePointWithEveryLegalPrediction() {
        for (count in MIN_GAME_PLAYERS..PRIVACY_MAX_PLAYERS) {
            for (yesCount in listOf(0, count)) {
                val points = (1 until count).map { privacyPoints(it, yesCount, count) }
                assertEquals(1, points.max())
                assertTrue(points.all { it in 0..1 })
            }
        }
    }

    @Test
    fun legacyPartialSessionAsksForCountAndRetainsTheAnswers() {
        val session = newRound(5)
        session.enter("Alex", true, 4)
        session.nextPlayer()
        session.enter("Sam", false, 2)
        val bytes = Base64.getDecoder().decode(session.serialize()).dropLast(Int.SIZE_BYTES).toByteArray()
        bytes[Int.SIZE_BYTES - 1] = 1
        val restored = PrivacySession.restore(Base64.getEncoder().encodeToString(bytes))
        assertTrue(restored.needsPlayerCount)
        assertEquals(1, restored.playerCount)
        assertEquals("Sam", restored.draftName)
        restored.configurePlayerCount(5)
        assertEquals(2, restored.draftPrediction)
        assertEquals(5, roundTrip(restored).totalPlayerCount)
    }

    private fun newRound(playerCount: Int = 2) = PrivacySession().apply {
        configurePlayerCount(playerCount)
        selectQuestion(QUESTION_ID, CARD_ID)
    }

    private fun PrivacySession.enter(name: String, yes: Boolean, prediction: Int) {
        draft.updateName(name)
        draft.chooseVote(yes)
        draft.choosePrediction(prediction)
    }

    private fun completedRound() = newRound(3).apply {
        enter("Alex", true, 2)
        nextPlayer()
        enter("Sam", false, 1)
        nextPlayer()
        enter("Chris", true, 1)
        reveal()
        startReveal()
        completeReveal()
    }

    private fun roundTrip(session: PrivacySession): PrivacySession {
        val saved = session.serialize()
        return PrivacySession.restore(saved).also { assertEquals(saved, it.serialize()) }
    }

    private companion object {
        const val QUESTION_ID = 101
        const val CARD_ID = 12L
    }
}
