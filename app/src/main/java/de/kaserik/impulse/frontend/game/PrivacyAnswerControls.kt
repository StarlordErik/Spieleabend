package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun PrivacyAnswerActions(session: PrivacySession) {
    val focusManager = LocalFocusManager.current
    Button(
        onClick = {
            focusManager.clearFocus()
            if (session.isLastPlayer) session.reveal() else session.nextPlayer()
        },
        enabled = if (session.isLastPlayer) session.canReveal else session.canGoToNextPlayer,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).heightIn(min = 52.dp),
    ) {
        Text(
            stringResource(if (session.isLastPlayer) R.string.privacy_evaluate else R.string.next_player),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyAnswerActionsPreview() {
    ImpulseTheme { PrivacyAnswerActions(remember { previewPrivacySession() }) }
}
