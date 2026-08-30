package de.impulse.spieleabend.frontend.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

internal enum class FunFactsPhase {
    SelectQuestion,
    EnterAnswer,
    PositionSign,
    FinalPositioning,
    Revealing,
    Complete,
}

internal data class FunFactsPlayer(
    val id: Int,
    val name: FunFactsDrawing,
    val answer: FunFactsDrawing,
    val colorIndex: Int,
    val revealed: Boolean = false,
)

internal data class FunFactsDrawing(
    val strokes: List<List<Offset>>,
)

@Stable
internal class FunFactsDraftDrawing(
    private val onChanged: () -> Unit = {},
) {
    val strokes = mutableStateListOf<List<Offset>>()

    fun startStroke(point: Offset) {
        strokes += listOf(point)
        onChanged()
    }

    fun continueStroke(point: Offset) {
        if (strokes.isEmpty()) return
        strokes[strokes.lastIndex] = strokes.last() + point
        onChanged()
    }

    fun clear() {
        if (strokes.isEmpty()) return
        strokes.clear()
        onChanged()
    }

    fun snapshot(): FunFactsDrawing =
        FunFactsDrawing(strokes.map { stroke -> stroke.toList() })

    fun restore(drawing: FunFactsDrawing) {
        strokes.clear()
        strokes.addAll(drawing.strokes)
    }
}

@Stable
@Suppress("TooManyFunctions")
internal class FunFactsSession(
    private val onChanged: () -> Unit = {},
) {
    var phase by mutableStateOf(FunFactsPhase.SelectQuestion)
        private set
    var selectedQuestionId by mutableStateOf<Int?>(null)
        private set
    var selectedColorIndex by mutableIntStateOf(0)
        private set
    var showFirstPlayerHint by mutableStateOf(false)
        private set
    val players = mutableStateListOf<FunFactsPlayer>()
    val draftName = FunFactsDraftDrawing(onChanged)
    val draftAnswer = FunFactsDraftDrawing(onChanged)

    private val knownNames = mutableStateListOf<FunFactsDrawing>()
    private val knownColors = mutableStateListOf<Int>()
    private var nextPlayerId by mutableIntStateOf(0)
    private var roundStartIndex by mutableIntStateOf(0)
    private var positioningPlayerId by mutableStateOf<Int?>(null)

    val selectingQuestion: Boolean
        get() = phase == FunFactsPhase.SelectQuestion

    val activeSignId: Int?
        get() = when (phase) {
            FunFactsPhase.PositionSign -> positioningPlayerId
            FunFactsPhase.FinalPositioning -> players.minByOrNull { it.id }?.id
            else -> null
        }

    val availableColorIndices: List<Int>
        get() {
            val reservedForCurrentPlayer = expectedKnownNameIndex()?.let(knownColors::getOrNull)
            return (0 until SIGN_COLOR_COUNT).filter { colorIndex ->
                players.none { it.colorIndex == colorIndex } &&
                    (colorIndex !in knownColors || colorIndex == reservedForCurrentPlayer)
            }
        }

    val canAddPlayer: Boolean
        get() = players.size < SIGN_COLOR_COUNT

    fun selectQuestion(questionId: Int) {
        selectedQuestionId = questionId
        phase = FunFactsPhase.EnterAnswer
        prepareDraftName()
        onChanged()
    }

    fun reopenQuestionSelection(): Int? {
        if (players.isNotEmpty()) return null
        val previous = selectedQuestionId
        selectedQuestionId = null
        draftAnswer.clear()
        phase = FunFactsPhase.SelectQuestion
        onChanged()
        return previous
    }

    fun selectColor(colorIndex: Int) {
        if (colorIndex !in availableColorIndices) return
        selectedColorIndex = colorIndex
        onChanged()
    }

    fun finishAnswer() {
        if (phase != FunFactsPhase.EnterAnswer) return
        val validDraft =
            draftName.strokes.isNotEmpty() &&
                draftAnswer.strokes.isNotEmpty() &&
                selectedColorIndex in availableColorIndices
        if (!validDraft) return

        val name = draftName.snapshot()
        val expectedNameIndex = expectedKnownNameIndex()
        if (expectedNameIndex != null) {
            knownNames[expectedNameIndex] = name
            while (knownColors.size <= expectedNameIndex) knownColors += UNASSIGNED_COLOR
            knownColors[expectedNameIndex] = selectedColorIndex
        } else {
            knownNames += name
            knownColors += selectedColorIndex
        }
        val player = FunFactsPlayer(
            id = nextPlayerId++,
            name = name,
            answer = draftAnswer.snapshot(),
            colorIndex = selectedColorIndex,
        )
        players += player
        positioningPlayerId = player.id
        phase = FunFactsPhase.PositionSign
        onChanged()
    }

    fun moveActiveSign(direction: Int) {
        val id = activeSignId ?: return
        val fromIndex = players.indexOfFirst { it.id == id }
        val toIndex = (fromIndex + direction).coerceIn(0, players.lastIndex)
        if (fromIndex >= 0 && fromIndex != toIndex) {
            val player = players.removeAt(fromIndex)
            players.add(toIndex, player)
            onChanged()
        }
    }

    fun nextPlayer() {
        if (phase != FunFactsPhase.PositionSign || !canAddPlayer) return
        draftName.clear()
        draftAnswer.clear()
        phase = FunFactsPhase.EnterAnswer
        prepareDraftName()
        onChanged()
    }

    fun beginReveal() {
        when (phase) {
            FunFactsPhase.PositionSign -> {
                if (players.size < 2) return
                phase = FunFactsPhase.FinalPositioning
                showFirstPlayerHint = true
            }
            FunFactsPhase.FinalPositioning -> {
                showFirstPlayerHint = false
                phase = FunFactsPhase.Revealing
                revealLowestSign()
            }
            FunFactsPhase.Revealing -> revealLowestSign()
            else -> return
        }
        onChanged()
    }

    fun dismissFirstPlayerHint() {
        showFirstPlayerHint = false
        onChanged()
    }

    fun startNextRound() {
        val lastRoundNames = players.sortedBy { it.id }.map { it.name }
        val lastRoundColors = players.sortedBy { it.id }.map { it.colorIndex }
        knownNames.clear()
        knownNames.addAll(lastRoundNames)
        knownColors.clear()
        knownColors.addAll(lastRoundColors)
        roundStartIndex = if (knownNames.size > 1) 1 else 0
        players.clear()
        positioningPlayerId = null
        selectedQuestionId = null
        draftName.clear()
        draftAnswer.clear()
        phase = FunFactsPhase.SelectQuestion
        prepareDraftName()
        onChanged()
    }

    fun serialize(): String = FunFactsSessionCodec.encode(this)

    private fun revealLowestSign() {
        val index = players.indexOfLast { !it.revealed }
        if (index < 0) {
            phase = FunFactsPhase.Complete
            return
        }
        players[index] = players[index].copy(revealed = true)
        if (players.all { it.revealed }) phase = FunFactsPhase.Complete
    }

    private fun prepareDraftName() {
        val knownNameIndex = expectedKnownNameIndex()
        knownNameIndex?.let { index -> draftName.restore(knownNames[index]) }
        selectedColorIndex = knownNameIndex
            ?.let(knownColors::getOrNull)
            ?.takeIf { it in availableColorIndices }
            ?: availableColorIndices.firstOrNull()
            ?: 0
    }

    private fun expectedKnownNameIndex(): Int? {
        if (knownNames.isEmpty() || players.size >= knownNames.size) return null
        return (roundStartIndex + players.size) % knownNames.size
    }

    private object FunFactsSessionCodec {
        fun encode(session: FunFactsSession): String = buildString {
            appendLine(SERIALIZATION_VERSION)
            appendLine(session.phase.name)
            appendLine(session.showFirstPlayerHint)
            appendLine(session.selectedQuestionId ?: NULL_INT)
            appendLine(session.selectedColorIndex)
            appendLine(session.nextPlayerId)
            appendLine(session.roundStartIndex)
            appendLine(session.positioningPlayerId ?: NULL_INT)
            appendLine(session.players.size)
            session.players.forEach { player ->
                appendLine(
                    listOf(
                        player.id,
                        player.colorIndex,
                        player.revealed,
                        encodeDrawing(player.name),
                        encodeDrawing(player.answer),
                    ).joinToString(FIELD_SEPARATOR),
                )
            }
            appendLine(session.knownNames.size)
            session.knownNames.forEach { drawing -> appendLine(encodeDrawing(drawing)) }
            appendLine(session.knownColors.size)
            session.knownColors.forEach { colorIndex -> appendLine(colorIndex) }
            appendLine(encodeDrawing(session.draftName.snapshot()))
            append(encodeDrawing(session.draftAnswer.snapshot()))
        }

        fun decode(
            serialized: String,
            onChanged: () -> Unit,
        ): FunFactsSession = runCatching {
            val lines = serialized.lineSequence().iterator()
            val serializationVersion = lines.next()
            require(
                serializationVersion == SERIALIZATION_VERSION ||
                    serializationVersion == PREVIOUS_SERIALIZATION_VERSION,
            )
            val session = FunFactsSession(onChanged)
            session.phase = FunFactsPhase.valueOf(lines.next())
            session.showFirstPlayerHint = lines.next().toBooleanStrict()
            session.selectedQuestionId = lines.next().toNullableInt()
            session.selectedColorIndex = lines.next().toInt()
            session.nextPlayerId = lines.next().toInt()
            session.roundStartIndex = lines.next().toInt()
            session.positioningPlayerId = lines.next().toNullableInt()
            repeat(lines.next().toInt()) {
                val fields = lines.next().split(FIELD_SEPARATOR)
                session.players += FunFactsPlayer(
                    id = fields[0].toInt(),
                    colorIndex = fields[1].toInt(),
                    revealed = fields[2].toBooleanStrict(),
                    name = decodeDrawing(fields[PLAYER_NAME_INDEX]),
                    answer = decodeDrawing(fields[PLAYER_ANSWER_INDEX]),
                )
            }
            repeat(lines.next().toInt()) {
                session.knownNames += decodeDrawing(lines.next())
            }
            if (serializationVersion == SERIALIZATION_VERSION) {
                repeat(lines.next().toInt()) {
                    session.knownColors += lines.next().toInt()
                }
            }
            session.draftName.restore(decodeDrawing(lines.next()))
            session.draftAnswer.restore(decodeDrawing(lines.next()))
            session
        }.getOrElse { FunFactsSession(onChanged) }

        private fun encodeDrawing(drawing: FunFactsDrawing): String =
            if (drawing.strokes.isEmpty()) {
                EMPTY_DRAWING
            } else {
                drawing.strokes.joinToString(STROKE_SEPARATOR) { stroke ->
                    stroke.joinToString(POINT_SEPARATOR) { point ->
                        "${point.x}$COORDINATE_SEPARATOR${point.y}"
                    }
                }
            }

        private fun decodeDrawing(serialized: String): FunFactsDrawing =
            if (serialized == EMPTY_DRAWING) {
                FunFactsDrawing(emptyList())
            } else {
                FunFactsDrawing(
                    serialized.split(STROKE_SEPARATOR).map { stroke ->
                        stroke.split(POINT_SEPARATOR).map { point ->
                            val coordinates = point.split(COORDINATE_SEPARATOR)
                            Offset(coordinates[0].toFloat(), coordinates[1].toFloat())
                        }
                    },
                )
            }

        private fun String.toNullableInt(): Int? = toInt().takeUnless { it == NULL_INT }
    }

    companion object {
        fun restore(
            serialized: String?,
            onChanged: () -> Unit = {},
        ): FunFactsSession =
            serialized?.let { FunFactsSessionCodec.decode(it, onChanged) } ?: FunFactsSession(onChanged)

        private const val SIGN_COLOR_COUNT = 10
        private const val UNASSIGNED_COLOR = -1
        private const val NULL_INT = -1
        private const val SERIALIZATION_VERSION = "3"
        private const val PREVIOUS_SERIALIZATION_VERSION = "2"
        private const val FIELD_SEPARATOR = ";"
        private const val STROKE_SEPARATOR = "|"
        private const val POINT_SEPARATOR = ","
        private const val COORDINATE_SEPARATOR = ":"
        private const val EMPTY_DRAWING = "-"
        private const val PLAYER_NAME_INDEX = 3
        private const val PLAYER_ANSWER_INDEX = 4
    }
}
