package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
internal fun PrivacyPredictionDial(value: Int, maximum: Int, onValueChange: (Int) -> Unit) {
    val dial = remember(maximum) { PrivacyDialState(value, maximum) }
    val changeValue by rememberUpdatedState(onValueChange)
    val label = stringResource(R.string.privacy_prediction)
    val angle by animateFloatAsState(
        dial.rotation,
        if (dial.dragging) snap() else tween(DIAL_SNAP_MILLIS),
        label = "privacyNumberWheel",
    )
    val displayedAngle by rememberUpdatedState(angle)
    val textMeasurer = rememberTextMeasurer()
    val numberStyle = MaterialTheme.typography.displaySmall.copy(
        color = colorResource(R.color.privacy_orange),
        fontSize = 44.sp,
        fontWeight = FontWeight.Bold,
    )
    val numbers = remember(maximum, numberStyle, textMeasurer) {
        (1..maximum).map { textMeasurer.measure(it.toString(), numberStyle) }
    }
    LaunchedEffect(value) {
        if (!dial.dragging && privacyDialValue(dial.rotation, maximum) != value) {
            dial.rotation = privacyDialTargetRotation(dial.rotation, value, maximum)
        }
    }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.size(width = 156.dp, height = 88.dp).semantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(
                value.toFloat(), 1f..maximum.toFloat(), (maximum - 2).coerceAtLeast(0),
            )
            setProgress { requested ->
                changeValue(requested.roundToInt().coerceIn(1, maximum))
                true
            }
        }.pointerInput(dial) {
            if (maximum == 1) return@pointerInput
            val pivot = Offset(size.width / 2f, size.height / 2f + dialRadius(maximum).toPx())
            var last = Offset.Zero
            detectDragGestures(
                onDragStart = { position ->
                    dial.rotation = displayedAngle
                    dial.dragging = true
                    last = position - pivot
                },
                onDrag = { change, _ ->
                    change.consume()
                    val position = change.position - pivot
                    val sensitivity = if (maximum <= 3) 2f else 1f
                    dial.rotation += privacyDialDelta(last, position) * sensitivity
                    changeValue(privacyDialValue(dial.rotation, maximum))
                    last = position
                },
                onDragEnd = dial::snapToNumber,
                onDragCancel = dial::snapToNumber,
            )
        },
    ) {
        Canvas(Modifier.size(width = 156.dp, height = 88.dp)) {
            val radius = dialRadius(maximum).toPx()
            val pivot = Offset(center.x, center.y + radius)
            // Only this window onto the top of the disc is drawn. The other numbers
            // turn around the same hidden centre and are clipped by the surface.
            numbers.forEachIndexed { index, number ->
                rotate(angle + index * DIAL_FULL_TURN / maximum, pivot) {
                    drawText(
                        number,
                        topLeft = Offset(center.x - number.size.width / 2f, center.y - number.size.height / 2f),
                    )
                }
            }
        }
    }
}

@Stable
private class PrivacyDialState(value: Int, private val maximum: Int) {
    var rotation by mutableFloatStateOf(-(value - 1) * DIAL_FULL_TURN / maximum)
    var dragging by mutableStateOf(false)

    fun snapToNumber() {
        rotation = privacyDialTargetRotation(rotation, privacyDialValue(rotation, maximum), maximum)
        dragging = false
    }
}

internal fun privacyDialDelta(from: Offset, to: Offset): Float {
    val radians = atan2(to.y, to.x) - atan2(from.y, from.x)
    return normalizedDialAngle(radians * DIAL_HALF_TURN / PI.toFloat())
}

internal fun privacyDialValue(rotation: Float, maximum: Int): Int =
    Math.floorMod((-rotation / (DIAL_FULL_TURN / maximum)).roundToInt(), maximum) + 1

internal fun privacyDialTargetRotation(rotation: Float, value: Int, maximum: Int): Float =
    rotation + normalizedDialAngle(-(value - 1) * DIAL_FULL_TURN / maximum - rotation)

private fun normalizedDialAngle(degrees: Float): Float =
    ((degrees + DIAL_HALF_TURN) % DIAL_FULL_TURN + DIAL_FULL_TURN) % DIAL_FULL_TURN - DIAL_HALF_TURN

private fun dialRadius(maximum: Int) = maxOf(72.dp, 18.dp * maximum)

@Preview(showBackground = true)
@Composable
private fun PrivacyPredictionDialPreview() {
    ImpulseTheme { PrivacyPredictionDial(value = 3, maximum = 4, onValueChange = {}) }
}

private const val DIAL_FULL_TURN = 360f
private const val DIAL_HALF_TURN = 180f
private const val DIAL_SNAP_MILLIS = 180
