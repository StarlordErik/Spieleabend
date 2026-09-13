package de.kaserik.impulse.frontend.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.Base64
import kotlin.math.abs

internal enum class PrivacyPhase { SelectQuestion, EnterAnswer, Revealing, Complete }

internal data class PrivacyPlayer(val id: Int, val name: String, val points: Int = 0)

internal data class PrivacyAnswer(val playerId: Int, val yes: Boolean, val prediction: Int)

internal data class PrivacyRanking(
    val player: PrivacyPlayer,
    val prediction: Int,
    val roundPoints: Int,
    val rank: Int,
)

internal fun privacyPoints(prediction: Int, yesCount: Int, playerCount: Int): Int {
    val points = when (abs(prediction - yesCount)) {
        0 -> PRIVACY_EXACT_POINTS
        1 -> 1
        else -> 0
    }
    return if (yesCount == playerCount) points.coerceAtMost(1) else points
}

@Stable
internal class PrivacySession(
    private val onChanged: () -> Unit = {},
    private val onRoundCompleted: (Int) -> Unit = {},
) {
    var phase by mutableStateOf(PrivacyPhase.SelectQuestion)
        private set
    var selectedQuestionId by mutableStateOf<Int?>(null)
        private set
    var cardInstanceId by mutableStateOf<Long?>(null)
        private set
    var roundNumber by mutableIntStateOf(1)
        private set
    val draft = PrivacyDraft(onChanged) { phase == PrivacyPhase.EnterAnswer }
    val draftName: String get() = draft.name
    val draftVote: Boolean? get() = draft.vote
    val draftPrediction: Int get() = draft.prediction

    private val players = mutableStateListOf<PrivacyPlayer>()
    private val answers = mutableStateListOf<PrivacyAnswer>()
    private val turnOrder = mutableStateListOf<Int>()
    private var nextId = 0

    val selectingQuestion: Boolean get() = phase == PrivacyPhase.SelectQuestion
    val playerNumber: Int get() = answers.size + 1
    val playerCount: Int get() = answers.size
    val yesCount: Int get() = answers.count { it.yes }
    val canChangeQuestion: Boolean get() = phase == PrivacyPhase.EnterAnswer && answers.isEmpty()
    val canSubmit: Boolean
        get() = phase == PrivacyPhase.EnterAnswer && draftName.isNotBlank() &&
                draftVote != null && answers.size < PRIVACY_MAX_PLAYERS
    val canGoToNextPlayer: Boolean get() = canSubmit && playerNumber < PRIVACY_MAX_PLAYERS
    val ranking: List<PrivacyRanking>
        get() {
            val participants = answers.map { answer -> players.first { it.id == answer.playerId } }
            return participants.sortedByDescending { it.points }.map { player ->
                val answer = answers.first { it.playerId == player.id }
                PrivacyRanking(
                    player = player,
                    prediction = answer.prediction,
                    roundPoints = privacyPoints(answer.prediction, yesCount, playerCount),
                    rank = participants.count { it.points > player.points } + 1,
                )
            }
        }

    fun selectQuestion(questionId: Int, instanceId: Long) {
        if (!selectingQuestion) return
        selectedQuestionId = questionId
        cardInstanceId = instanceId
        phase = PrivacyPhase.EnterAnswer
        onChanged()
    }

    fun reopenQuestionSelection(): Int? {
        if (!canChangeQuestion) return null
        val previous = selectedQuestionId
        selectedQuestionId = null
        cardInstanceId = null
        draft.reset(draftName)
        phase = PrivacyPhase.SelectQuestion
        onChanged()
        return previous
    }

    fun nextPlayer() {
        if (!canGoToNextPlayer) return
        saveAnswer()
        prepareDraft()
        onChanged()
    }

    fun reveal() {
        if (!canSubmit) return
        saveAnswer()
        draft.reset()
        phase = PrivacyPhase.Revealing
        onChanged()
    }

    fun completeReveal() {
        if (phase != PrivacyPhase.Revealing) return
        answers.forEach { answer ->
            val index = players.indexOfFirst { it.id == answer.playerId }
            val player = players[index]
            players[index] = player.copy(
                points = player.points + privacyPoints(answer.prediction, yesCount, playerCount),
            )
        }
        phase = PrivacyPhase.Complete
        selectedQuestionId?.let(onRoundCompleted)
        onChanged()
    }

    fun startNextRound() {
        if (phase != PrivacyPhase.Complete) return
        val participants = answers.map { it.playerId }
        turnOrder.clear()
        turnOrder.addAll(participants.drop(1) + participants.take(1))
        players.removeAll { it.id !in participants }
        answers.clear()
        selectedQuestionId = null
        cardInstanceId = null
        roundNumber++
        phase = PrivacyPhase.SelectQuestion
        prepareDraft()
        onChanged()
    }

    fun restartGame() {
        players.clear()
        answers.clear()
        turnOrder.clear()
        nextId = 0
        roundNumber = 1
        selectedQuestionId = null
        cardInstanceId = null
        phase = PrivacyPhase.SelectQuestion
        prepareDraft()
        onChanged()
    }

    fun onCardChanged(instanceId: Long) {
        if (cardInstanceId == null || cardInstanceId == instanceId) return
        if (phase == PrivacyPhase.Complete) {
            startNextRound()
            return
        }
        val participantOrder = (answers.map { it.playerId } + turnOrder).distinct()
        turnOrder.clear()
        turnOrder.addAll(participantOrder)
        answers.clear()
        selectedQuestionId = null
        cardInstanceId = null
        phase = PrivacyPhase.SelectQuestion
        prepareDraft()
        onChanged()
    }

    private fun saveAnswer() {
        val knownIndex = players.indexOfFirst { it.id == turnOrder.getOrNull(answers.size) }
        val player = if (knownIndex >= 0) {
            players[knownIndex].copy(name = draftName.trim()).also { players[knownIndex] = it }
        } else {
            PrivacyPlayer(nextId++, draftName.trim()).also { players += it }
        }
        answers += PrivacyAnswer(player.id, draftVote == true, draftPrediction)
    }

    private fun prepareDraft() {
        draft.reset(players.firstOrNull { it.id == turnOrder.getOrNull(answers.size) }?.name.orEmpty())
    }

    fun serialize(): String {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { output ->
            output.writeInt(PRIVACY_SESSION_VERSION)
            output.writeUTF(phase.name)
            output.writeInt(selectedQuestionId ?: -1)
            output.writeLong(cardInstanceId ?: -1L)
            output.writeInt(roundNumber)
            output.writeInt(nextId)
            output.writeUTF(draftName)
            output.writeInt(draftVote?.let { if (it) 1 else 0 } ?: -1)
            output.writeInt(draftPrediction)
            output.writeInt(players.size)
            players.forEach { player ->
                output.writeInt(player.id)
                output.writeUTF(player.name)
                output.writeInt(player.points)
            }
            output.writeInt(turnOrder.size)
            turnOrder.forEach(output::writeInt)
            output.writeInt(answers.size)
            answers.forEach { answer ->
                output.writeInt(answer.playerId)
                output.writeBoolean(answer.yes)
                output.writeInt(answer.prediction)
            }
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray())
    }

    companion object {
        fun restore(
            serialized: String?,
            onChanged: () -> Unit = {},
            onRoundCompleted: (Int) -> Unit = {},
        ): PrivacySession = runCatching {
            val bytes = Base64.getDecoder().decode(requireNotNull(serialized))
            DataInputStream(ByteArrayInputStream(bytes)).use { input ->
                require(input.readInt() == PRIVACY_SESSION_VERSION)
                PrivacySession(onChanged, onRoundCompleted).apply {
                    phase = PrivacyPhase.valueOf(input.readUTF())
                    selectedQuestionId = input.readInt().takeIf { it >= 0 }
                    cardInstanceId = input.readLong().takeIf { it >= 0 }
                    roundNumber = input.readInt().also { require(it > 0) }
                    nextId = input.readInt().also { require(it >= 0) }
                    draft.restore(input)
                    readPlayers(input)
                    validateRestoredRound()
                }
            }
        }.getOrElse { PrivacySession(onChanged, onRoundCompleted) }

        private fun PrivacySession.readPlayers(input: DataInputStream) {
            repeat(input.readPlayerCount()) {
                players += PrivacyPlayer(input.readInt(), input.readUTF(), input.readInt())
            }
            repeat(input.readPlayerCount()) { turnOrder += input.readInt() }
            repeat(input.readPlayerCount()) {
                answers += PrivacyAnswer(input.readInt(), input.readBoolean(), input.readInt())
            }
        }

        private fun PrivacySession.validateRestoredRound() {
            require(players.all { it.id in 0 until nextId && it.name.isNotBlank() && it.points >= 0 })
            require(players.map { it.id }.distinct().size == players.size)
            require(turnOrder.distinct().size == turnOrder.size && turnOrder.all { id -> players.any { it.id == id } })
            require(answers.map { it.playerId }.distinct().size == answers.size)
            require(answers.all { answer ->
                players.any { it.id == answer.playerId } && answer.prediction in 1..PRIVACY_MAX_PLAYERS
            })
            if (selectingQuestion) {
                require(answers.isEmpty())
            } else {
                require(selectedQuestionId != null && cardInstanceId != null)
                require(phase != PrivacyPhase.EnterAnswer || answers.size < PRIVACY_MAX_PLAYERS)
                require(phase == PrivacyPhase.EnterAnswer || answers.isNotEmpty())
            }
        }
    }


}

private fun DataInputStream.readPlayerCount(): Int =
    readInt().also { require(it in 0..PRIVACY_MAX_PLAYERS) }

internal const val PRIVACY_MAX_PLAYERS = 10
private const val PRIVACY_EXACT_POINTS = 3
private const val PRIVACY_SESSION_VERSION = 1
