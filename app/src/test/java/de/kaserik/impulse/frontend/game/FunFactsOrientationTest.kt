package de.kaserik.impulse.frontend.game

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FunFactsOrientationTest {
    @Test
    fun emptyNameSelectsNameSignOnRotation() {
        assertEquals(
            FunFactsLandscapeTarget.Name,
            landscapeDrawingTarget(null, rotationEnabled = true, isLandscape = true, hasName = false),
        )
    }

    @Test
    fun existingNameSelectsAnswerSignOnRotation() {
        assertEquals(
            FunFactsLandscapeTarget.Answer,
            landscapeDrawingTarget(null, rotationEnabled = true, isLandscape = true, hasName = true),
        )
    }

    @Test
    fun writingAndClearingNameKeepNameSignUntilReturningToPortrait() {
        val draft = FunFactsDraftDrawing()
        var target = landscapeDrawingTarget(null, true, true, draft.strokes.isNotEmpty())

        draft.startStroke(Offset(0.2f, 0.3f))
        target = landscapeDrawingTarget(target, true, true, draft.strokes.isNotEmpty())
        assertEquals(FunFactsLandscapeTarget.Name, target)

        draft.clear()
        assertEquals(
            FunFactsLandscapeTarget.Name,
            landscapeDrawingTarget(target, true, true, draft.strokes.isNotEmpty()),
        )
    }

    @Test
    fun returningToPortraitAllowsAnswerSignOnNextRotationWithoutLosingDrawing() {
        val draft = FunFactsDraftDrawing()
        val nameTarget = landscapeDrawingTarget(null, true, true, false)
        draft.startStroke(Offset(0.2f, 0.3f))
        val drawing = draft.snapshot()

        val portraitTarget = landscapeDrawingTarget(nameTarget, true, false, true)
        assertNull(portraitTarget)
        assertEquals(
            FunFactsLandscapeTarget.Answer,
            landscapeDrawingTarget(portraitTarget, true, true, draft.strokes.isNotEmpty()),
        )
        assertEquals(drawing, draft.snapshot())
    }

    @Test
    fun restoredLandscapeTargetSurvivesAnotherLandscapeConfiguration() {
        assertEquals(
            FunFactsLandscapeTarget.Name,
            landscapeDrawingTarget(FunFactsLandscapeTarget.Name, true, true, true),
        )
        assertEquals(
            FunFactsLandscapeTarget.Answer,
            landscapeDrawingTarget(FunFactsLandscapeTarget.Answer, true, true, false),
        )
    }

    @Test
    fun disablingRotationClearsEitherSignEvenWhileDisplayIsStillLandscape() {
        FunFactsLandscapeTarget.entries.forEach { target ->
            assertNull(landscapeDrawingTarget(target, false, true, true))
        }
        assertNull(landscapeDrawingTarget(null, false, true, false))
    }
}
