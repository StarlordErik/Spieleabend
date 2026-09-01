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
    val strokeWidthIndex: Int = DEFAULT_STROKE_WIDTH_INDEX,
    val revealed: Boolean = false,
    val answerVisible: Boolean = revealed,
)

internal data class FunFactsDrawing(
    val strokes: List<List<Offset>>,
    val strokeWidthFractions: List<Float> = emptyList(),
)

@Stable
internal class FunFactsDraftDrawing(
    private val onChanged: () -> Unit = {},
) {
    val strokes = mutableStateListOf<List<Offset>>()
    private val strokeWidthFractions = mutableStateListOf<Float>()

    fun startStroke(
        point: Offset,
        strokeWidthFraction: Float = DEFAULT_DRAWING_STROKE_WIDTH_FRACTION,
    ) {
        strokes += listOf(point)
        strokeWidthFractions += strokeWidthFraction
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
        strokeWidthFractions.clear()
        onChanged()
    }

    fun snapshot(): FunFactsDrawing =
        FunFactsDrawing(
            strokes = strokes.map { stroke -> stroke.toList() },
            strokeWidthFractions = strokeWidthFractions.toList(),
        )

    fun restore(drawing: FunFactsDrawing) {
        strokes.clear()
        strokes.addAll(drawing.strokes)
        strokeWidthFractions.clear()
        strokeWidthFractions.addAll(
            drawing.strokes.indices.map { index ->
                drawing.strokeWidthFractions.getOrNull(index)
                    ?: DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
            },
        )
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
    var selectedStrokeWidthIndex by mutableIntStateOf(DEFAULT_STROKE_WIDTH_INDEX)
        private set
    var showFirstPlayerHint by mutableStateOf(false)
        private set
    val players = mutableStateListOf<FunFactsPlayer>()
    val draftName = FunFactsDraftDrawing(onChanged)
    val draftAnswer = FunFactsDraftDrawing(onChanged)

    private val knownNames = mutableStateListOf<FunFactsDrawing>()
    private val knownColors = mutableStateListOf<Int>()
    private val knownStrokeWidthIndices = mutableStateListOf<Int>()
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

    fun selectStrokeWidth(index: Int) {
        if (index !in 0 until STROKE_WIDTH_OPTION_COUNT) return
        selectedStrokeWidthIndex = index
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
            while (knownStrokeWidthIndices.size <= expectedNameIndex) {
                knownStrokeWidthIndices += DEFAULT_STROKE_WIDTH_INDEX
            }
            knownStrokeWidthIndices[expectedNameIndex] = selectedStrokeWidthIndex
        } else {
            knownNames += name
            knownColors += selectedColorIndex
            knownStrokeWidthIndices += selectedStrokeWidthIndex
        }
        val player = FunFactsPlayer(
            id = nextPlayerId++,
            name = name,
            answer = draftAnswer.snapshot(),
            colorIndex = selectedColorIndex,
            strokeWidthIndex = selectedStrokeWidthIndex,
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

    fun toggleRevealedSide(playerId: Int) {
        if (phase != FunFactsPhase.Revealing && phase != FunFactsPhase.Complete) return
        val playerIndex = players.indexOfFirst { it.id == playerId }
        if (playerIndex >= 0) {
            val player = players[playerIndex]
            if (player.revealed) {
                players[playerIndex] = player.copy(answerVisible = !player.answerVisible)
                onChanged()
            }
        }
    }

    fun restartGame() {
        phase = FunFactsPhase.SelectQuestion
        selectedQuestionId = null
        selectedColorIndex = 0
        selectedStrokeWidthIndex = DEFAULT_STROKE_WIDTH_INDEX
        showFirstPlayerHint = false
        players.clear()
        draftName.clear()
        draftAnswer.clear()
        knownNames.clear()
        knownColors.clear()
        knownStrokeWidthIndices.clear()
        nextPlayerId = 0
        roundStartIndex = 0
        positioningPlayerId = null
        onChanged()
    }

    fun startNextRound() {
        val lastRoundNames = players.sortedBy { it.id }.map { it.name }
        val lastRoundColors = players.sortedBy { it.id }.map { it.colorIndex }
        val lastRoundStrokeWidthIndices = players.sortedBy { it.id }.map { it.strokeWidthIndex }
        knownNames.clear()
        knownNames.addAll(lastRoundNames)
        knownColors.clear()
        knownColors.addAll(lastRoundColors)
        knownStrokeWidthIndices.clear()
        knownStrokeWidthIndices.addAll(lastRoundStrokeWidthIndices)
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
        players.indices.forEach { index ->
            val player = players[index]
            if (player.revealed && !player.answerVisible) {
                players[index] = player.copy(answerVisible = true)
            }
        }
        val index = players.indexOfLast { !it.revealed }
        if (index < 0) {
            phase = FunFactsPhase.Complete
            return
        }
        players[index] = players[index].copy(revealed = true, answerVisible = true)
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
        selectedStrokeWidthIndex = knownNameIndex
            ?.let(knownStrokeWidthIndices::getOrNull)
            ?.takeIf { it in 0 until STROKE_WIDTH_OPTION_COUNT }
            ?: DEFAULT_STROKE_WIDTH_INDEX
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
            appendLine(session.selectedStrokeWidthIndex)
            appendLine(session.nextPlayerId)
            appendLine(session.roundStartIndex)
            appendLine(session.positioningPlayerId ?: NULL_INT)
            appendLine(session.players.size)
            session.players.forEach { player ->
                appendLine(
                    listOf(
                        player.id,
                        player.colorIndex,
                        player.strokeWidthIndex,
                        player.revealed,
                        player.answerVisible,
                        encodeDrawing(player.name),
                        encodeDrawing(player.answer),
                    ).joinToString(FIELD_SEPARATOR),
                )
            }
            appendLine(session.knownNames.size)
            session.knownNames.forEach { drawing -> appendLine(encodeDrawing(drawing)) }
            appendLine(session.knownColors.size)
            session.knownColors.forEach { colorIndex -> appendLine(colorIndex) }
            appendLine(session.knownStrokeWidthIndices.size)
            session.knownStrokeWidthIndices.forEach { index -> appendLine(index) }
            appendLine(encodeDrawing(session.draftName.snapshot()))
            append(encodeDrawing(session.draftAnswer.snapshot()))
        }

        fun decode(
            serialized: String,
            onChanged: () -> Unit,
        ): FunFactsSession = runCatching {
            val lines = serialized.lineSequence().iterator()
            val serializationVersion = lines.next()
            require(serializationVersion in SUPPORTED_SERIALIZATION_VERSIONS)
            val features = SerializationFeatures(serializationVersion)
            val session = FunFactsSession(onChanged)
            decodeSessionState(lines, session, features)
            repeat(lines.next().toInt()) {
                session.players += decodePlayer(lines.next(), features)
            }
            decodeKnownPlayers(lines, session, features)
            session.draftName.restore(decodeDrawing(lines.next(), features.strokeWidths))
            session.draftAnswer.restore(decodeDrawing(lines.next(), features.strokeWidths))
            session
        }.getOrElse { FunFactsSession(onChanged) }

        private fun decodeSessionState(
            lines: Iterator<String>,
            session: FunFactsSession,
            features: SerializationFeatures,
        ) {
            session.phase = FunFactsPhase.valueOf(lines.next())
            session.showFirstPlayerHint = lines.next().toBooleanStrict()
            session.selectedQuestionId = lines.next().toNullableInt()
            session.selectedColorIndex = lines.next().toInt()
            if (features.strokeWidthPreferences) {
                session.selectedStrokeWidthIndex = lines.next().toInt()
            }
            session.nextPlayerId = lines.next().toInt()
            session.roundStartIndex = lines.next().toInt()
            session.positioningPlayerId = lines.next().toNullableInt()
        }

        private fun decodePlayer(
            serialized: String,
            features: SerializationFeatures,
        ): FunFactsPlayer {
            val fields = serialized.split(FIELD_SEPARATOR)
            val revealed = fields[features.playerRevealedIndex].toBooleanStrict()
            return FunFactsPlayer(
                id = fields[0].toInt(),
                colorIndex = fields[1].toInt(),
                strokeWidthIndex = if (features.strokeWidthPreferences) {
                    fields[PLAYER_STROKE_WIDTH_INDEX].toInt()
                } else {
                    DEFAULT_STROKE_WIDTH_INDEX
                },
                revealed = revealed,
                answerVisible = if (features.answerVisibility) {
                    fields[features.playerAnswerVisibleIndex].toBooleanStrict()
                } else {
                    revealed
                },
                name = decodeDrawing(fields[features.playerNameIndex], features.strokeWidths),
                answer = decodeDrawing(fields[features.playerAnswerIndex], features.strokeWidths),
            )
        }

        private fun decodeKnownPlayers(
            lines: Iterator<String>,
            session: FunFactsSession,
            features: SerializationFeatures,
        ) {
            repeat(lines.next().toInt()) {
                session.knownNames += decodeDrawing(lines.next(), features.strokeWidths)
            }
            if (features.knownColors) {
                repeat(lines.next().toInt()) {
                    session.knownColors += lines.next().toInt()
                }
            }
            if (features.strokeWidthPreferences) {
                repeat(lines.next().toInt()) {
                    session.knownStrokeWidthIndices += lines.next().toInt()
                }
            } else {
                repeat(session.knownNames.size) {
                    session.knownStrokeWidthIndices += DEFAULT_STROKE_WIDTH_INDEX
                }
            }
        }

        private data class SerializationFeatures(val version: String) {
            val answerVisibility = version in ANSWER_VISIBILITY_VERSIONS
            val strokeWidths = version in STROKE_WIDTH_VERSIONS
            val strokeWidthPreferences = version == SERIALIZATION_VERSION
            val knownColors = version != LEGACY_SERIALIZATION_VERSION
            val playerRevealedIndex = if (strokeWidthPreferences) {
                PLAYER_REVEALED_INDEX
            } else {
                OLD_PLAYER_REVEALED_INDEX
            }
            val playerAnswerVisibleIndex = if (strokeWidthPreferences) {
                PLAYER_ANSWER_VISIBLE_INDEX
            } else {
                OLD_PLAYER_ANSWER_VISIBLE_INDEX
            }
            val playerNameIndex = when {
                strokeWidthPreferences -> PLAYER_NAME_INDEX
                answerVisibility -> OLD_PLAYER_NAME_WITH_VISIBILITY_INDEX
                else -> OLD_PLAYER_NAME_INDEX
            }
            val playerAnswerIndex = when {
                strokeWidthPreferences -> PLAYER_ANSWER_INDEX
                answerVisibility -> OLD_PLAYER_ANSWER_WITH_VISIBILITY_INDEX
                else -> OLD_PLAYER_ANSWER_INDEX
            }
        }

        private fun encodeDrawing(drawing: FunFactsDrawing): String =
            if (drawing.strokes.isEmpty()) {
                EMPTY_DRAWING
            } else {
                drawing.strokes.mapIndexed { strokeIndex, stroke ->
                    val strokeWidth = drawing.strokeWidthFractions.getOrNull(strokeIndex)
                        ?: DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
                    val points = stroke.joinToString(POINT_SEPARATOR) { point ->
                        "${point.x}$COORDINATE_SEPARATOR${point.y}"
                    }
                    "$strokeWidth$STROKE_WIDTH_SEPARATOR$points"
                }.joinToString(STROKE_SEPARATOR)
            }

        private fun decodeDrawing(
            serialized: String,
            includesStrokeWidths: Boolean,
        ): FunFactsDrawing =
            if (serialized == EMPTY_DRAWING) {
                FunFactsDrawing(emptyList())
            } else {
                val encodedStrokes = serialized.split(STROKE_SEPARATOR)
                val strokeWidths = mutableListOf<Float>()
                FunFactsDrawing(
                    strokes = encodedStrokes.map { encodedStroke ->
                        val stroke = if (includesStrokeWidths) {
                            val strokeParts = encodedStroke.split(STROKE_WIDTH_SEPARATOR, limit = 2)
                            strokeWidths += strokeParts.first().toFloat()
                            strokeParts.last()
                        } else {
                            strokeWidths += DEFAULT_DRAWING_STROKE_WIDTH_FRACTION
                            encodedStroke
                        }
                        stroke.split(POINT_SEPARATOR).map { point ->
                            val coordinates = point.split(COORDINATE_SEPARATOR)
                            Offset(coordinates[0].toFloat(), coordinates[1].toFloat())
                        }
                    },
                    strokeWidthFractions = strokeWidths,
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
        private const val STROKE_WIDTH_OPTION_COUNT = 3
        private const val UNASSIGNED_COLOR = -1
        private const val NULL_INT = -1
        private const val SERIALIZATION_VERSION = "6"
        private const val PREVIOUS_SERIALIZATION_VERSION = "5"
        private const val OLDER_SERIALIZATION_VERSION = "4"
        private const val EARLIER_SERIALIZATION_VERSION = "3"
        private const val LEGACY_SERIALIZATION_VERSION = "2"
        private val SUPPORTED_SERIALIZATION_VERSIONS = setOf(
            SERIALIZATION_VERSION,
            PREVIOUS_SERIALIZATION_VERSION,
            OLDER_SERIALIZATION_VERSION,
            EARLIER_SERIALIZATION_VERSION,
            LEGACY_SERIALIZATION_VERSION,
        )
        private val ANSWER_VISIBILITY_VERSIONS = setOf(
            SERIALIZATION_VERSION,
            PREVIOUS_SERIALIZATION_VERSION,
            OLDER_SERIALIZATION_VERSION,
        )
        private val STROKE_WIDTH_VERSIONS = setOf(
            SERIALIZATION_VERSION,
            PREVIOUS_SERIALIZATION_VERSION,
        )
        private const val FIELD_SEPARATOR = ";"
        private const val STROKE_SEPARATOR = "|"
        private const val POINT_SEPARATOR = ","
        private const val COORDINATE_SEPARATOR = ":"
        private const val STROKE_WIDTH_SEPARATOR = "~"
        private const val EMPTY_DRAWING = "-"
        private const val PLAYER_STROKE_WIDTH_INDEX = 2
        private const val PLAYER_REVEALED_INDEX = 3
        private const val PLAYER_ANSWER_VISIBLE_INDEX = 4
        private const val PLAYER_NAME_INDEX = 5
        private const val PLAYER_ANSWER_INDEX = 6
        private const val OLD_PLAYER_REVEALED_INDEX = 2
        private const val OLD_PLAYER_ANSWER_VISIBLE_INDEX = 3
        private const val OLD_PLAYER_NAME_WITH_VISIBILITY_INDEX = 4
        private const val OLD_PLAYER_ANSWER_WITH_VISIBILITY_INDEX = 5
        private const val OLD_PLAYER_NAME_INDEX = 3
        private const val OLD_PLAYER_ANSWER_INDEX = 4
    }
}

internal const val DEFAULT_DRAWING_STROKE_WIDTH_FRACTION = 0.0225f
internal const val DEFAULT_STROKE_WIDTH_INDEX = 1
