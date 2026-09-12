package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.frontend.resources.displayNameRes
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun CardTextEditorDialog(
    cardText: GameKartentextUiModel,
    language: Sprache,
    onSave: (String) -> Unit,
    onDeleteOwnTranslation: () -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by rememberSaveable(cardText.id, cardText.text, language) {
        mutableStateOf(if (cardText.uebersetzungFehlt) "" else cardText.text)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_card_text_title, stringResource(language.displayNameRes()))) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (cardText.uebersetzungFehlt) {
                    Text(stringResource(R.string.add_translation_info, stringResource(language.displayNameRes())))
                    Text(cardText.text, modifier = Modifier.padding(top = 12.dp))
                } else {
                    Text(stringResource(R.string.own_translation_info))
                }
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .padding(top = 12.dp),
                    label = { Text(stringResource(R.string.own_card_text)) },
                    minLines = 4,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft) },
                enabled = draft.isNotBlank(),
            ) {
                Text(stringResource(if (cardText.uebersetzungFehlt) R.string.add_translation else R.string.save))
            }
        },
        dismissButton = {
            Row {
                if (cardText.eigeneLokalisierungFuerAktuelleSprache) {
                    TextButton(onClick = onDeleteOwnTranslation) {
                        Text(stringResource(R.string.delete_own_translation))
                    }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CardTextEditorDialogPreview() {
    ImpulseTheme {
        CardTextEditorDialog(
            cardText = GameKartentextUiModel(
                id = 1,
                text = stringResource(R.string.preview_question_moment),
                kategorieId = 1,
                gespielt = false,
                eigeneLokalisierung = true,
                eigeneLokalisierungFuerAktuelleSprache = true,
            ),
            language = Sprache.DE,
            onSave = {},
            onDeleteOwnTranslation = {},
            onDismiss = {},
        )
    }
}
