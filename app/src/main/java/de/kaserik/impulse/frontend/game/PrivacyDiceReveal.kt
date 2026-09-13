package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
internal fun PrivacyDiceReveal(
    yesCount: Int,
    playerCount: Int,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }
    val finish by rememberUpdatedState(onFinished)
    val dice = remember(yesCount, playerCount) { List(playerCount) { it < yesCount }.shuffled() }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(PRIVACY_REVEAL_MILLIS, easing = LinearEasing))
        delay(PRIVACY_DICE_HOLD_MILLIS)
        finish()
    }
    Column(
        modifier = Modifier.widthIn(max = 440.dp).then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(
            stringResource(R.string.privacy_revealing),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.privacy_mixed_votes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        PrivacyDiceScene(dice, progress.value, Modifier.weight(1f, fill = false).fillMaxWidth())
        if (progress.value >= 1f) PrivacyVoteSummary(yesCount, playerCount)
    }
}

@Composable
private fun PrivacyDiceScene(dice: List<Boolean>, progress: Float, modifier: Modifier = Modifier) {
    val orange = colorResource(R.color.privacy_orange)
    val bag = colorResource(R.color.privacy_bag)
    val seam = colorResource(R.color.privacy_bag_seam)
    val description = stringResource(R.string.privacy_dice_animation)
    Canvas(
        modifier = modifier.aspectRatio(DICE_SCENE_WIDTH / DICE_SCENE_HEIGHT)
            .semantics { contentDescription = description }) {
        withTransform({
            scale(
                size.width / DICE_SCENE_WIDTH,
                size.height / DICE_SCENE_HEIGHT,
                pivot = Offset.Zero
            )
        }) {
            val tableShadow = Rect(Offset(30f, 173f), Size(268f, 83f))
            drawOval(Color.Black.copy(alpha = 0.15f), tableShadow.topLeft, tableShadow.size)
            drawPrivacyBag(progress, bag, seam)
            dice.forEachIndexed { index, yes ->
                drawFallingPrivacyDie(index, dice.size, progress, if (yes) orange else Color.Black)
            }
        }
    }
}

private fun DrawScope.drawPrivacyBag(progress: Float, bag: Color, seam: Color) {
    val tilt = ((progress - 0.08f) / 0.22f).coerceIn(0f, 1f) * 118f
    val shake = sin(progress * 50f) * 3f * (1f - progress)
    val origin = Offset(116f, 78f)
    translate(origin.x, origin.y + shake) {
        rotate(tilt, pivot = Offset.Zero) {
            val silhouette = Path().apply {
                moveTo(-22f, -46f)
                cubicTo(-28f, -25f, -53f, -4f, -47f, 29f)
                cubicTo(-40f, 60f, 40f, 60f, 47f, 29f)
                cubicTo(53f, -4f, 28f, -25f, 22f, -46f)
                close()
            }
            drawPath(silhouette, bag)
            drawPath(silhouette, seam, style = Stroke(2f))
            drawPrivacyBagDetails(bag, seam)
        }
    }
}

private fun DrawScope.drawPrivacyBagDetails(bag: Color, seam: Color) {
    val opening = Rect(Offset(-23f, -53f), Size(46f, 15f))
    val rim = Rect(Offset(-17f, -50f), Size(34f, 8f))
    val collarStart = Offset(-27f, -34f)
    val collarEnd = Offset(27f, -34f)
    val knot = Offset(20f, -34f)
    val cordEnds = listOf(Offset(42f, -16f), Offset(38f, -38f))
    val stitchBounds = Rect(Offset(-34f, -10f), Size(68f, 52f))
    val stitchStart = 20f
    val stitchSweep = 140f
    drawOval(seam, opening.topLeft, opening.size)
    drawOval(bag, rim.topLeft, rim.size, style = Stroke(2f))
    drawLine(seam, collarStart, collarEnd, BAG_COLLAR_STROKE, StrokeCap.Round)
    cordEnds.forEach { end -> drawLine(seam, knot, end, BAG_CORD_STROKE, StrokeCap.Round) }
    drawArc(
        seam.copy(alpha = 0.4f), stitchStart, stitchSweep, false,
        stitchBounds.topLeft, stitchBounds.size, style = Stroke(2f)
    )
}

private fun DrawScope.drawFallingPrivacyDie(index: Int, count: Int, progress: Float, color: Color) {
    val fall = ((progress - 0.29f - index * 0.035f) / 0.35f).coerceIn(0f, 1f)
    if (fall <= 0f) return
    val columns = minOf(count, 5)
    val rowCount = minOf(count - (index / 5) * 5, 5)
    val targetX = 160f + (index % columns - (rowCount - 1) / 2f) * 49f
    val targetY = 188f + (index / 5) * 48f + sin(index * 2f) * 6f
    val x = 159f + (targetX - 159f) * fall
    val y = 99f + (targetY - 99f) * fall - sin(fall * PI).toFloat() * 30f
    val angle = (1f - fall) * (if (index % 2 == 0) 300f else -280f) + (index % 3 - 1) * 13f
    val shadow = Rect(Offset(x - 19f, targetY + 14f), Size(40f, 9f))
    drawOval(Color.Black.copy(alpha = 0.2f * fall), shadow.topLeft, shadow.size)
    translate(x, y) {
        rotate(angle, Offset.Zero) {
            drawPrivacyDie(
                color,
                index % DIE_FACE_COUNT + 1
            )
        }
    }
}

private fun DrawScope.drawPrivacyDie(color: Color, pips: Int) {
    val bounds = Rect(-18f, -18f, 18f, 18f)
    val corners = CornerRadius(7f)
    val outline = Stroke(1.4f)
    val pipRadius = 2.7f
    drawRoundRect(color, bounds.topLeft, bounds.size, corners)
    drawRoundRect(
        Color.White.copy(alpha = 0.35f),
        bounds.topLeft,
        bounds.size,
        corners,
        style = outline
    )
    val positions = buildList {
        if (pips % 2 == 1) add(Offset.Zero)
        if (pips >= 2) {
            add(Offset(-9f, -9f)); add(Offset(9f, 9f))
        }
        if (pips >= 4) {
            add(Offset(9f, -9f)); add(Offset(-9f, 9f))
        }
        if (pips == 6) {
            add(Offset(-9f, 0f)); add(Offset(9f, 0f))
        }
    }
    positions.forEach { drawCircle(Color.White, pipRadius, it) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 500)
@Composable
private fun PrivacyDiceRevealPreview() {
    ImpulseTheme { PrivacyDiceReveal(PRIVACY_MAX_PLAYERS / 2, PRIVACY_MAX_PLAYERS, {}) }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PrivacyDiceScenePreview() {
    ImpulseTheme { PrivacyDiceScene(listOf(true, false, true, false, true), 1f) }
}

private const val PRIVACY_REVEAL_MILLIS = 3600
private const val PRIVACY_DICE_HOLD_MILLIS = 750L
private const val DICE_SCENE_WIDTH = 320f
private const val DICE_SCENE_HEIGHT = 270f
private const val BAG_COLLAR_STROKE = 4f
private const val BAG_CORD_STROKE = 3f
private const val DIE_FACE_COUNT = 6
