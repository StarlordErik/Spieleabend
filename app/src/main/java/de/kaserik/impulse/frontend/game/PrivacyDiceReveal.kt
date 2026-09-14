package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
internal fun PrivacyDiceReveal(
    session: PrivacySession,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
    nextRoundEnabled: Boolean = true,
) {
    val awaitingReveal = session.phase == PrivacyPhase.AwaitingReveal
    val complete = session.phase == PrivacyPhase.Complete
    val progress = remember { Animatable(if (complete) 1f else 0f) }
    val compact = remember { Animatable(if (complete) 1f else 0f) }
    val dice = remember(session.roundNumber, session.selectedQuestionId) {
        List(session.playerCount) { it < session.yesCount }
            .shuffled(Random(session.roundNumber xor (session.selectedQuestionId ?: 0)))
    }
    val placements = remember(session.roundNumber, session.selectedQuestionId, dice.size) {
        privacyDiceLayout(dice.size, Random(session.roundNumber xor (session.selectedQuestionId ?: 0)))
    }
    LaunchedEffect(session.phase) {
        if (complete) {
            compact.animateTo(1f, tween(PRIVACY_DICE_MOVE_MILLIS))
        } else if (session.phase == PrivacyPhase.Revealing) {
            progress.animateTo(1f, tween(PRIVACY_REVEAL_MILLIS, easing = LinearEasing))
            delay(PRIVACY_DICE_HOLD_MILLIS)
            session.completeReveal()
        }
    }
    BoxWithConstraints(
        modifier = Modifier.widthIn(max = 440.dp).then(modifier),
    ) {
        PrivacyDiceScene(dice, placements, progress.value, compact.value, Modifier.fillMaxSize())
        if (awaitingReveal) {
            val unit = maxWidth / DICE_SCENE_WIDTH
            val sceneTop = ((maxHeight - unit * DICE_SCENE_HEIGHT) / 2f).coerceAtLeast(0.dp)
            val label = stringResource(R.string.privacy_open_bag)
            Box(
                Modifier.offset(x = unit * BAG_TOUCH_BOUNDS.left, y = sceneTop + unit * BAG_TOUCH_BOUNDS.top)
                    .size(width = unit * BAG_TOUCH_BOUNDS.width, height = unit * BAG_TOUCH_BOUNDS.height)
                    .semantics { contentDescription = label }
                    .clickable(role = Role.Button, onClick = session::startReveal),
            )
        }
        if (compact.value < 1f) {
            Text(
                stringResource(if (awaitingReveal) R.string.privacy_open_bag else R.string.privacy_revealing),
                modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = 1f - compact.value },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        val diceHeight = maxWidth * PRIVACY_DICE_LANDING_AREA.height / DICE_SCENE_WIDTH
        if (complete) PrivacyResults(
            session = session,
            onNextRound = onNextRound,
            nextRoundEnabled = nextRoundEnabled && compact.value >= 1f,
            modifier = Modifier.fillMaxSize().padding(top = diceHeight + 16.dp).graphicsLayer {
                alpha = compact.value
                translationY = (1f - compact.value) * 28.dp.toPx()
            },
        )
    }
}

@Composable
private fun PrivacyDiceScene(
    dice: List<Boolean>,
    placements: List<PrivacyDiePlacement>,
    progress: Float,
    compact: Float,
    modifier: Modifier = Modifier,
) {
    val orange = colorResource(R.color.privacy_dice_orange)
    val bag = colorResource(R.color.privacy_bag)
    val seam = colorResource(R.color.privacy_bag_seam)
    val description = if (progress < 1f) stringResource(R.string.privacy_dice_animation)
    else stringResource(R.string.privacy_dice_result, dice.count { it }, dice.count { !it })
    Canvas(
        modifier = modifier.semantics { if (progress > 0f) contentDescription = description }) {
        val scale = size.width / DICE_SCENE_WIDTH
        val sceneTop = ((size.height / scale - DICE_SCENE_HEIGHT) / 2f).coerceAtLeast(0f)
        withTransform({
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            if (compact < 1f) {
                val pileOffset = Offset(0f, sceneTop * (1f - compact) - PRIVACY_DICE_LANDING_AREA.top * compact)
                drawOval(
                    Color.Black.copy(alpha = 0.15f * (1f - compact)),
                    PRIVACY_DICE_LANDING_AREA.topLeft + pileOffset,
                    PRIVACY_DICE_LANDING_AREA.size,
                )
            }
            if (compact < 1f) translate(top = sceneTop) {
                drawPrivacyBag(progress, bag.copy(alpha = 1f - compact), seam.copy(alpha = 1f - compact))
            }
            dice.forEachIndexed { index, yes ->
                drawFallingPrivacyDie(index, placements[index], progress, if (yes) orange else Color.Black, sceneTop, compact)
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

private fun DrawScope.drawFallingPrivacyDie(
    index: Int,
    placement: PrivacyDiePlacement,
    progress: Float,
    color: Color,
    sceneTop: Float,
    compact: Float,
) {
    val fall = ((progress - 0.29f - index * 0.035f) / 0.35f).coerceIn(0f, 1f)
    if (fall <= 0f) return
    val targetX = placement.center.x
    val targetY = placement.center.y
    val x = 159f + (targetX - 159f) * fall
    val revealY = sceneTop + 99f + (targetY - 99f) * fall - sin(fall * PI).toFloat() * 30f
    val parkedY = targetY - PRIVACY_DICE_LANDING_AREA.top
    val y = revealY + (parkedY - revealY) * compact
    val angle = (1f - fall) * (if (index % 2 == 0) 300f else -280f) + placement.rotation
    val shadowY = (sceneTop + targetY) * (1f - compact) + parkedY * compact
    val shadow = Rect(Offset(x - 14.25f, shadowY + 10.5f), Size(30f, 6.75f))
    drawOval(Color.Black.copy(alpha = 0.2f * fall), shadow.topLeft, shadow.size)
    translate(x, y) {
        rotate(angle, Offset.Zero) {
            drawPrivacyDie(color)
        }
    }
}

private fun DrawScope.drawPrivacyDie(color: Color) {
    val front = Color.White.copy(alpha = 0.14f).compositeOver(color)
    val side = Color.Black.copy(alpha = 0.3f).compositeOver(front)
    val top = Color.White.copy(alpha = 0.32f).compositeOver(color)
    drawPath(DIE_BODY, front)
    drawPath(DIE_RIGHT_FACE, side)
    drawPath(DIE_TOP_FACE, top)
}

@Preview(showBackground = true, widthDp = 360, heightDp = 500)
@Composable
private fun PrivacyDiceRevealPreview() {
    ImpulseTheme { PrivacyDiceReveal(remember { previewPrivacyResults() }, {}) }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 500)
@Composable
private fun PrivacyDiceScenePreview() {
    val dice = List(PRIVACY_MAX_PLAYERS) { it % 2 == 0 }
    val placements = remember { privacyDiceLayout(dice.size, Random(12)) }
    ImpulseTheme { PrivacyDiceScene(dice, placements, 1f, 0f, Modifier.fillMaxSize()) }
}

private const val PRIVACY_REVEAL_MILLIS = 3600
private const val PRIVACY_DICE_HOLD_MILLIS = 750L
private const val PRIVACY_DICE_MOVE_MILLIS = 650
private const val DICE_SCENE_WIDTH = 320f
private const val DICE_SCENE_HEIGHT = 270f
private const val BAG_COLLAR_STROKE = 4f
private const val BAG_CORD_STROKE = 3f
private val BAG_TOUCH_BOUNDS = Rect(62f, 23f, 170f, 140f)
private val DIE_HALF_WIDTH = PRIVACY_DIE_RADIUS * sqrt(3f) / 2f
private val DIE_BODY = Path().apply {
    moveTo(0f, -PRIVACY_DIE_RADIUS)
    lineTo(DIE_HALF_WIDTH, -PRIVACY_DIE_RADIUS / 2f)
    lineTo(DIE_HALF_WIDTH, PRIVACY_DIE_RADIUS / 2f)
    lineTo(0f, PRIVACY_DIE_RADIUS)
    lineTo(-DIE_HALF_WIDTH, PRIVACY_DIE_RADIUS / 2f)
    lineTo(-DIE_HALF_WIDTH, -PRIVACY_DIE_RADIUS / 2f)
    close()
}
private val DIE_RIGHT_FACE = Path().apply {
    moveTo(0f, 0f)
    lineTo(DIE_HALF_WIDTH, -PRIVACY_DIE_RADIUS / 2f)
    lineTo(DIE_HALF_WIDTH, PRIVACY_DIE_RADIUS / 2f)
    lineTo(0f, PRIVACY_DIE_RADIUS)
    close()
}
private val DIE_TOP_FACE = Path().apply {
    moveTo(0f, -PRIVACY_DIE_RADIUS)
    lineTo(DIE_HALF_WIDTH, -PRIVACY_DIE_RADIUS / 2f)
    lineTo(0f, 0f)
    lineTo(-DIE_HALF_WIDTH, -PRIVACY_DIE_RADIUS / 2f)
    close()
}
