package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun PrivacyPlayerHeader(session: PrivacySession, compact: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            stringResource(
                R.string.privacy_player,
                session.playerNumber,
                PRIVACY_MAX_PLAYERS
            ),
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.privacy_round, session.roundNumber),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
    }
    if (session.canChangeQuestion && !compact) {
        Text(
            stringResource(R.string.privacy_change_question),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun PrivacyAnswerActions(session: PrivacySession) {
    val focusManager = LocalFocusManager.current
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { focusManager.clearFocus(); session.nextPlayer() },
            enabled = session.canGoToNextPlayer,
            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
        ) {
            Text(
                stringResource(R.string.privacy_next_player),
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = { focusManager.clearFocus(); session.reveal() },
            enabled = session.canSubmit,
            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
        ) { Text(stringResource(R.string.privacy_evaluate)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPlayerHeaderPreview() {
    ImpulseTheme {
        Column { PrivacyPlayerHeader(remember { previewPrivacySession() }, false) }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyAnswerActionsPreview() {
    ImpulseTheme { PrivacyAnswerActions(remember { previewPrivacySession() }) }
}
