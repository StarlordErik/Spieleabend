package de.impulse.spieleabend.frontend.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme
import kotlin.math.roundToInt

@Composable
fun AppSettingsDialog(
    developerMode: Boolean,
    onDeveloperModeChanged: (Boolean) -> Unit,
    onResetAllCards: () -> Unit,
    onDismiss: () -> Unit,
) {
    var infoText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Einstellungen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Dev-Zugang", modifier = Modifier.weight(1f))
                    TextButton(onClick = { infoText = DEV_ACCESS_INFO }) { Text("ⓘ") }
                    Switch(checked = developerMode, onCheckedChange = onDeveloperModeChanged)
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = onResetAllCards, modifier = Modifier.weight(1f)) {
                        Text("Alle Karten zurücksetzen")
                    }
                    TextButton(onClick = { infoText = GLOBAL_RESET_INFO }) { Text("ⓘ") }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Schließen") } },
    )

    infoText?.let { text ->
        SettingsInfoDialog(text = text, onDismiss = { infoText = null })
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSettingsDialogPreview() {
    SpieleabendTheme {
        AppSettingsDialog(
            developerMode = true,
            onDeveloperModeChanged = {},
            onResetAllCards = {},
            onDismiss = {},
        )
    }
}

@Composable
@Suppress("LongMethod")
fun GameSettingsDialog(
    textsPerCard: Int,
    defaultTextsPerCard: Int,
    developerMode: Boolean,
    supportsFunFactsMode: Boolean = false,
    funFactsModeEnabled: Boolean = false,
    onFunFactsModeChanged: (Boolean) -> Unit = {},
    onRestartFunFactsGame: () -> Unit = {},
    onResetSeenCards: () -> Unit,
    onResetAllCards: () -> Unit,
    onTextsPerCardChanged: (Int) -> Unit,
    onResetTextsPerCard: () -> Unit,
    onShowCards: () -> Unit,
    onDismiss: () -> Unit,
) {
    var infoText by remember { mutableStateOf<String?>(null) }
    var sliderValue by remember { mutableFloatStateOf(textsPerCard.toFloat()) }
    LaunchedEffect(textsPerCard) { sliderValue = textsPerCard.toFloat() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Spieleinstellungen") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (supportsFunFactsMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Spielmodus", style = MaterialTheme.typography.titleSmall)
                            Text(
                                if (funFactsModeEnabled) "Fun Facts" else "Basic (Swipe-Karten)",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Switch(
                            checked = funFactsModeEnabled,
                            onCheckedChange = onFunFactsModeChanged,
                        )
                    }
                    if (funFactsModeEnabled) {
                        Button(
                            onClick = onRestartFunFactsGame,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Spiel neu starten")
                        }
                    }
                    HorizontalDivider()
                }
                SettingsActionRow(
                    label = "Gesehene Karten zurücksetzen",
                    onClick = onResetSeenCards,
                    onInfoClick = { infoText = RESET_SEEN_INFO },
                )
                SettingsActionRow(
                    label = "Alle Karten zurücksetzen",
                    onClick = onResetAllCards,
                    onInfoClick = { infoText = GAME_RESET_INFO },
                )
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kartentexte pro Karte: ${sliderValue.roundToInt()}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    TextButton(onClick = { infoText = TEXT_COUNT_INFO }) { Text("ⓘ") }
                }
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it.roundToInt()
                            .coerceIn(MIN_TEXTS_PER_CARD, MAX_TEXTS_PER_CARD)
                            .toFloat()
                    },
                    onValueChangeFinished = { onTextsPerCardChanged(sliderValue.roundToInt()) },
                    valueRange = MIN_TEXTS_PER_CARD.toFloat()..MAX_TEXTS_PER_CARD.toFloat(),
                    steps = 3,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            sliderValue = defaultTextsPerCard.toFloat()
                            onResetTextsPerCard()
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Standard ($defaultTextsPerCard) wiederherstellen")
                    }
                    TextButton(onClick = { infoText = TEXT_COUNT_DEFAULT_INFO }) { Text("ⓘ") }
                }
                if (developerMode) {
                    HorizontalDivider()
                    Button(onClick = onShowCards, modifier = Modifier.fillMaxWidth()) {
                        Text("Karten zeigen")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Schließen") } },
    )

    infoText?.let { text ->
        SettingsInfoDialog(text = text, onDismiss = { infoText = null })
    }
}

@Preview(showBackground = true)
@Composable
private fun GameSettingsDialogPreview() {
    SpieleabendTheme {
        GameSettingsDialog(
            textsPerCard = 3,
            defaultTextsPerCard = 2,
            developerMode = true,
            onResetSeenCards = {},
            onResetAllCards = {},
            onTextsPerCardChanged = {},
            onResetTextsPerCard = {},
            onShowCards = {},
            onDismiss = {},
        )
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = onClick, modifier = Modifier.weight(1f)) { Text(label) }
        TextButton(onClick = onInfoClick) { Text("ⓘ") }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsActionRowPreview() {
    SpieleabendTheme {
        SettingsActionRow(label = "Aktion", onClick = {}, onInfoClick = {})
    }
}

@Composable
private fun SettingsInfoDialog(
    text: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Information") },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsInfoDialogPreview() {
    SpieleabendTheme { SettingsInfoDialog(text = RESET_SEEN_INFO, onDismiss = {}) }
}

private const val DEV_ACCESS_INFO =
    "Aktiviert zusätzliche Werkzeuge für die Entwicklung und Kontrolle der Kartendaten."
private const val GLOBAL_RESET_INFO =
    "Setzt bei allen Kartentexten aller Spiele ‚gesehen‘ und ‚gespielt‘ zurück."
private const val RESET_SEEN_INFO =
    "Setzt ‚gesehen‘ nur bei noch nicht gespielten Kartentexten zurück."
private const val GAME_RESET_INFO =
    "Setzt bei allen Kartentexten dieses Spiels ‚gesehen‘ und ‚gespielt‘ zurück."
private const val TEXT_COUNT_INFO =
    "Legt für zukünftige Ziehungen fest, wie viele Texte eine Karte enthält."
private const val TEXT_COUNT_DEFAULT_INFO =
    "Entfernt die persönliche Auswahl und verwendet wieder den Standard dieses Spiels."
private const val MIN_TEXTS_PER_CARD = 1
private const val MAX_TEXTS_PER_CARD = 5
