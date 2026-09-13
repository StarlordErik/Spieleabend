package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
internal fun PrivacyAnswerEntry(session: PrivacySession, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    BoxWithConstraints(Modifier.widthIn(max = 440.dp).then(modifier)) {
        val compact = maxHeight < 520.dp
        key(session.playerNumber) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(top = if (compact) 0.dp else 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 16.dp),
            ) {
                PrivacyPlayerHeader(session, compact)
                OutlinedTextField(
                    value = session.draftName,
                    onValueChange = session.draft::updateName,
                    label = { Text(stringResource(R.string.player_name_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                PrivacyVoteChoices(session.draftVote, compact = compact, onVote = {
                    focusManager.clearFocus()
                    session.draft.chooseVote(it)
                })
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        stringResource(R.string.privacy_prediction),
                        style = MaterialTheme.typography.titleMedium
                    )
                    PrivacyPredictionDial(
                        session.draftPrediction,
                        session.draft::choosePrediction,
                        compact
                    )
                    if (!compact) {
                        Text(
                            stringResource(R.string.privacy_dial_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                PrivacyAnswerActions(session)
            }
        }
    }
}

@Composable
private fun PrivacyVoteChoices(
    selected: Boolean?,
    onVote: (Boolean) -> Unit,
    compact: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(true, false).forEach { yes ->
                PrivacyVoteButton(
                    yes = yes,
                    selected = selected == yes,
                    onClick = { onVote(yes) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (!compact) {
            Text(
                stringResource(R.string.privacy_secret_vote),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PrivacyVoteButton(
    yes: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(if (yes) R.string.privacy_yes else R.string.privacy_no)
    Surface(
        modifier = modifier.heightIn(min = 56.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (yes) colorResource(R.color.privacy_orange) else Color.Black,
        contentColor = if (yes) Color.Black else Color.White,
        border = BorderStroke(
            if (selected) 3.dp else 1.dp,
            if (selected) Color.White else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
            Text(
                if (selected) stringResource(R.string.privacy_selected_vote, label) else label,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun PrivacyPredictionDial(
    value: Int,
    onValueChange: (Int) -> Unit,
    compact: Boolean = false
) {
    val currentValue by rememberUpdatedState(value)
    val changeValue by rememberUpdatedState(onValueChange)
    val label = stringResource(R.string.privacy_prediction)
    val orange = colorResource(R.color.privacy_orange)
    val dialSize = if (compact) 80.dp else 112.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        IconButton(
            onClick = { onValueChange(value - 1) }, enabled = value > 1,
            modifier = Modifier.semantics { contentDescription = "− $label" }) {
            Text("−", style = MaterialTheme.typography.headlineMedium)
        }
        Surface(
            shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.size(dialSize).semantics {
                contentDescription = label
                progressBarRangeInfo = ProgressBarRangeInfo(
                    value.toFloat(), 1f..PRIVACY_MAX_PLAYERS.toFloat(), PRIVACY_MAX_PLAYERS - 2,
                )
                setProgress { changeValue(it.roundToInt().coerceIn(1, PRIVACY_MAX_PLAYERS)); true }
            }.privacyDialInput({ currentValue }, { changeValue(it) }),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(dialSize)) {
                    repeat(PRIVACY_MAX_PLAYERS * DIAL_TICKS_PER_STEP) { tick ->
                        val angle = tick * 2 * PI / (PRIVACY_MAX_PLAYERS * DIAL_TICKS_PER_STEP)
                        val direction = Offset(sin(angle).toFloat(), -cos(angle).toFloat())
                        drawLine(
                            color = orange.copy(alpha = if (tick < value * DIAL_TICKS_PER_STEP) 1f else 0.18f),
                            start = center + direction * (size.minDimension / 2 - 10.dp.toPx()),
                            end = center + direction * (size.minDimension / 2 - 5.dp.toPx()),
                            strokeWidth = 2.dp.toPx(),
                        )
                    }
                }
                Text(
                    value.toString(),
                    fontSize = if (compact) 36.sp else 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = orange
                )
            }
        }
        IconButton(
            onClick = { onValueChange(value + 1) }, enabled = value < PRIVACY_MAX_PLAYERS,
            modifier = Modifier.semantics { contentDescription = "+ $label" }) {
            Text("+", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

internal fun privacyDialDelta(from: Offset, to: Offset): Float {
    val radians = atan2(to.y, to.x) - atan2(from.y, from.x)
    val halfTurn = DIAL_FULL_TURN / 2
    val degrees = radians * halfTurn / PI.toFloat()
    val normalized = (degrees + DIAL_FULL_TURN + halfTurn) % DIAL_FULL_TURN - halfTurn
    return normalized / (DIAL_FULL_TURN / PRIVACY_MAX_PLAYERS)
}

private fun Modifier.privacyDialInput(
    currentValue: () -> Int,
    changeValue: (Int) -> Unit
): Modifier =
    pointerInput(Unit) {
        var last = Offset.Zero
        var rotation = 0f
        var vertical = false
        var dragValue = currentValue()
        detectDragGestures(
            onDragStart = { position ->
                last = position - Offset(size.width / 2f, size.height / 2f)
                rotation = 0f
                vertical = last.getDistance() < size.width / DIAL_CENTER_DIAMETER_RATIO
                dragValue = currentValue()
            },
            onDrag = { change, amount ->
                change.consume()
                val position = change.position - Offset(size.width / 2f, size.height / 2f)
                rotation += if (vertical) {
                    -amount.y / size.height * PRIVACY_MAX_PLAYERS
                } else {
                    privacyDialDelta(last, position)
                }
                val steps = rotation.toInt()
                if (steps != 0) {
                    dragValue = (dragValue + steps).coerceIn(1, PRIVACY_MAX_PLAYERS)
                    changeValue(dragValue)
                    rotation -= steps
                }
                last = position
            },
        )
    }

@Preview(showBackground = true, widthDp = 364, heightDp = 570)
@Composable
private fun PrivacyAnswerEntryPreview() {
    ImpulseTheme { PrivacyAnswerEntry(remember { previewPrivacySession() }) }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyVoteChoicesPreview() {
    ImpulseTheme { PrivacyVoteChoices(true, {}) }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyVoteButtonPreview() {
    ImpulseTheme { PrivacyVoteButton(yes = true, selected = true, onClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPredictionDialPreview() {
    ImpulseTheme { PrivacyPredictionDial(PRIVACY_MAX_PLAYERS / 2, {}) }
}

private const val DIAL_TICKS_PER_STEP = 3
private const val DIAL_FULL_TURN = 360f
private const val DIAL_CENTER_DIAMETER_RATIO = 4f
