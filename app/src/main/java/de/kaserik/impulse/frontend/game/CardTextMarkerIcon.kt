@file:Suppress("MagicNumber")

package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun CardTextMarkerIcon(
    marker: CardTextMarker,
    checked: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    Canvas(modifier = modifier.size(17.dp)) {
        when (marker) {
            CardTextMarker.BROKEN_HEART -> drawBrokenHeart(checked, color, backgroundColor)
            CardTextMarker.STAR -> drawStar(checked, color)
            CardTextMarker.PENCIL -> drawPencil(checked, color)
        }
    }
}

private fun DrawScope.drawMarkerPath(path: Path, checked: Boolean, color: Color) {
    if (checked) drawPath(path, color)
    // Keep the same outer contour when the marker is filled.
    drawPath(path, color, style = Stroke(width = MarkerStrokeWidth.toPx()))
}

private fun DrawScope.drawBrokenHeart(checked: Boolean, color: Color, backgroundColor: Color) {
    val (left, right) = brokenHeartPaths(size.width, size.height)
    drawMarkerPath(left, checked, color)
    drawMarkerPath(right, checked, color)
    if (checked) {
        drawPath(
            path = brokenHeartCrackPath(size.width, size.height),
            color = backgroundColor,
            style = Stroke(width = 2.1.dp.toPx()),
        )
    }
}

private fun DrawScope.drawStar(checked: Boolean, color: Color) {
    val path = Path()
    repeat(STAR_POINT_COUNT * 2) { index ->
        val radius = if (index % 2 == 0) size.minDimension * 0.48f else size.minDimension * 0.21f
        val angle = -PI / 2.0 + index * PI / STAR_POINT_COUNT
        val x = size.width / 2f + (cos(angle) * radius).toFloat()
        val y = size.height / 2f + (sin(angle) * radius).toFloat()
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawMarkerPath(path, checked, color)
}

private fun DrawScope.drawPencil(checked: Boolean, color: Color) {
    rotate(degrees = -45f) {
        val body = Path().apply {
            moveTo(size.width * 0.38f, size.height * 0.12f)
            lineTo(size.width * 0.62f, size.height * 0.12f)
            lineTo(size.width * 0.62f, size.height * 0.7f)
            lineTo(size.width * 0.38f, size.height * 0.7f)
            close()
        }
        val tip = Path().apply {
            moveTo(size.width * 0.38f, size.height * 0.7f)
            lineTo(size.width * 0.62f, size.height * 0.7f)
            lineTo(size.width * 0.5f, size.height * 0.92f)
            close()
        }
        drawMarkerPath(body, checked, color)
        drawMarkerPath(tip, checked, color)
    }
}

private fun brokenHeartPaths(width: Float, height: Float): Pair<Path, Path> {
    val left = Path().apply {
        moveTo(width * 0.46f, height * 0.88f)
        cubicTo(width * 0.34f, height * 0.76f, width * 0.06f, height * 0.58f, width * 0.08f, height * 0.3f)
        cubicTo(width * 0.1f, height * 0.06f, width * 0.39f, height * 0.02f, width * 0.5f, height * 0.24f)
        lineTo(width * 0.42f, height * 0.43f)
        lineTo(width * 0.52f, height * 0.53f)
        lineTo(width * 0.4f, height * 0.67f)
        close()
    }
    val right = Path().apply {
        moveTo(width * 0.54f, height * 0.88f)
        cubicTo(width * 0.66f, height * 0.76f, width * 0.94f, height * 0.58f, width * 0.92f, height * 0.3f)
        cubicTo(width * 0.9f, height * 0.06f, width * 0.61f, height * 0.02f, width * 0.5f, height * 0.24f)
        lineTo(width * 0.58f, height * 0.43f)
        lineTo(width * 0.48f, height * 0.53f)
        lineTo(width * 0.6f, height * 0.67f)
        close()
    }
    return left to right
}

private fun brokenHeartCrackPath(width: Float, height: Float): Path =
    Path().apply {
        moveTo(width * 0.5f, height * 0.22f)
        lineTo(width * 0.42f, height * 0.43f)
        lineTo(width * 0.52f, height * 0.53f)
        lineTo(width * 0.4f, height * 0.67f)
        lineTo(width * 0.47f, height * 0.88f)
    }

@Preview(showBackground = true)
@Composable
private fun CardTextMarkerIconPreview() {
    ImpulseTheme { CardTextMarkerIcon(CardTextMarker.STAR, checked = true) }
}

internal val MarkerStrokeWidth = 1.6.dp
private const val STAR_POINT_COUNT = 5
