package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.common.Sprache
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
internal fun CardTextEditorDialog(
    cardText: GameKartentextUiModel,
    language: Sprache,
    onSave: (String) -> Unit,
    onDeleteOwnTranslation: () -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember(cardText.id, cardText.text) { mutableStateOf(cardText.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kartentext bearbeiten (${language.displayName()})") },
        text = {
            Column {
                Text("Gespeichert wird nur deine eigene Lokalisierung.")
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .padding(top = 12.dp),
                    label = { Text("Eigener Kartentext") },
                    minLines = 4,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft) },
                enabled = draft.isNotBlank(),
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Row {
                if (cardText.eigeneLokalisierungFuerAktuelleSprache) {
                    TextButton(onClick = onDeleteOwnTranslation) {
                        Text("Eigene löschen")
                    }
                }
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CardTextEditorDialogPreview() {
    SpieleabendTheme {
        CardTextEditorDialog(
            cardText = GameKartentextUiModel(
                id = 1,
                text = "Was war heute dein schönster Moment?",
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

private fun Sprache.displayName(): String =
    when (this) {
        Sprache.DE -> "Deutsch"
        Sprache.EN -> "Englisch"
        Sprache.ERIK -> "Erik/Deutsch"
        Sprache.OG, Sprache.EIGENE_DE, Sprache.EIGENE_EN -> name
    }
