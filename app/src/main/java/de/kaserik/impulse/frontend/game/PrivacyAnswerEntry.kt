package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun PrivacyAnswerEntry(session: PrivacySession, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    Box(Modifier.widthIn(max = 440.dp).then(modifier)) {
        key(session.playerNumber) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
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
                PrivacyVoteChoices(session.draftVote, onVote = {
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
                        value = session.draftPrediction,
                        maximum = session.predictionMaximum,
                        onValueChange = session.draft::choosePrediction,
                    )
                }
                PrivacyAnswerActions(session)
            }
        }
    }
}

@Composable
private fun PrivacyVoteChoices(
    selected: Boolean?,
    onVote: (Boolean) -> Unit
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
