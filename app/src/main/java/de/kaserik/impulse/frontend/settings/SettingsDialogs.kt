@file:Suppress("TooManyFunctions")

package de.kaserik.impulse.frontend.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.common.Sprache
import de.kaserik.impulse.domain.model.BearbeiteteKartentexteModus
import de.kaserik.impulse.domain.model.FavoritenModus
import de.kaserik.impulse.domain.model.GeloeschteKartentexteModus
import de.kaserik.impulse.frontend.game.CardTextMarker
import de.kaserik.impulse.frontend.resources.displayNameRes
import de.kaserik.impulse.frontend.theme.ImpulseTheme
import kotlin.math.roundToInt

@Composable
fun AppSettingsDialog(
    developerMode: Boolean,
    language: Sprache,
    onDeveloperModeChanged: (Boolean) -> Unit,
    onLanguageChanged: (Sprache) -> Unit,
    onResetAllCards: () -> Unit,
    onDismiss: () -> Unit,
    customTranslationActions: CustomTranslationActions = CustomTranslationActions(),
) {
    var infoText by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(stringResource(R.string.language), style = MaterialTheme.typography.titleSmall)
                Sprache.AuswaehlbareSprachen.forEach { availableLanguage ->
                    SettingsRadioOption(
                        label = stringResource(availableLanguage.displayNameRes()),
                        selected = language == availableLanguage,
                        onClick = { onLanguageChanged(availableLanguage) },
                    )
                }
                HorizontalDivider()
                Text(
                    stringResource(R.string.edited_card_texts),
                    style = MaterialTheme.typography.titleSmall
                )
                CustomTranslationSettings(allGames = true, actions = customTranslationActions)
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.developer_access), modifier = Modifier.weight(1f))
                    TextButton(onClick = { infoText = R.string.developer_access_info }) {
                        Text(stringResource(R.string.symbol_information))
                    }
                    Switch(checked = developerMode, onCheckedChange = onDeveloperModeChanged)
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = onResetAllCards, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.reset_all_cards))
                    }
                    TextButton(onClick = { infoText = R.string.global_reset_info }) {
                        Text(stringResource(R.string.symbol_information))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } },
    )

    infoText?.let { text ->
        SettingsInfoDialog(text = stringResource(text), onDismiss = { infoText = null })
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSettingsDialogPreview() {
    ImpulseTheme {
        AppSettingsDialog(
            developerMode = true,
            language = Sprache.DE,
            onDeveloperModeChanged = {},
            onLanguageChanged = {},
            onResetAllCards = {},
            onDismiss = {},
        )
    }
}

@Composable
@Suppress("LongMethod")
fun GameSettingsDialog(
    settings: GameSettingsState,
    settingsActions: GameSettingsActions = GameSettingsActions(),
    onRestartFunFactsGame: () -> Unit = {},
    onRestartPrivacyGame: () -> Unit = {},
    onShowCards: () -> Unit,
    onDismiss: () -> Unit,
) {
    var infoText by remember { mutableStateOf<Int?>(null) }
    var sliderValue by remember { mutableFloatStateOf(settings.textsPerCard.toFloat()) }
    LaunchedEffect(settings.textsPerCard) { sliderValue = settings.textsPerCard.toFloat() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.game_settings)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GameModeSettings(
                    settings,
                    settingsActions,
                    onRestartFunFactsGame,
                    onRestartPrivacyGame
                )
                CardTextSymbolSettings(settings, settingsActions)
                HorizontalDivider()
                SettingsActionRow(
                    label = stringResource(R.string.reset_seen_cards),
                    onClick = settingsActions.onResetSeenCards,
                    onInfoClick = { infoText = R.string.reset_seen_info },
                )
                SettingsActionRow(
                    label = stringResource(R.string.reset_all_cards),
                    onClick = settingsActions.onResetAllCards,
                    onInfoClick = { infoText = R.string.game_reset_info },
                )
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.texts_per_card, sliderValue.roundToInt()),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    TextButton(onClick = { infoText = R.string.text_count_info }) {
                        Text(stringResource(R.string.symbol_information))
                    }
                }
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it.roundToInt()
                            .coerceIn(MIN_TEXTS_PER_CARD, MAX_TEXTS_PER_CARD)
                            .toFloat()
                    },
                    onValueChangeFinished = { settingsActions.onTextsPerCardChanged(sliderValue.roundToInt()) },
                    valueRange = MIN_TEXTS_PER_CARD.toFloat()..MAX_TEXTS_PER_CARD.toFloat(),
                    steps = 3,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            sliderValue = settings.defaultTextsPerCard.toFloat()
                            settingsActions.onResetTextsPerCard()
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            stringResource(
                                R.string.restore_default_text_count,
                                settings.defaultTextsPerCard
                            )
                        )
                    }
                    TextButton(onClick = { infoText = R.string.text_count_default_info }) {
                        Text(stringResource(R.string.symbol_information))
                    }
                }
                if (settings.developerMode) {
                    HorizontalDivider()
                    Button(onClick = onShowCards, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.show_cards))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } },
    )

    infoText?.let { text ->
        SettingsInfoDialog(text = stringResource(text), onDismiss = { infoText = null })
    }
}

@Composable
private fun GameModeSettings(
    settings: GameSettingsState,
    settingsActions: GameSettingsActions,
    onRestartFunFactsGame: () -> Unit,
    onRestartPrivacyGame: () -> Unit,
) {
    if (settings.supportsFunFactsMode || settings.supportsPrivacyMode) {
        val modeEnabled =
            if (settings.supportsPrivacyMode) settings.privacyModeEnabled else settings.funFactsModeEnabled
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.game_mode),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    stringResource(
                        when {
                            !modeEnabled -> R.string.mode_basic
                            settings.supportsPrivacyMode -> R.string.mode_privacy
                            else -> R.string.mode_fun_facts
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Switch(
                checked = modeEnabled,
                onCheckedChange = if (settings.supportsPrivacyMode) {
                    settingsActions.onPrivacyModeChanged
                } else {
                    settingsActions.onFunFactsModeChanged
                },
            )
        }
        if (modeEnabled) {
            Button(
                onClick = if (settings.supportsPrivacyMode) onRestartPrivacyGame else onRestartFunFactsGame,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.restart_game))
            }
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun GameModeSettingsPreview() {
    ImpulseTheme {
        Column {
            GameModeSettings(
                GameSettingsState(
                    textsPerCard = 2,
                    defaultTextsPerCard = 2,
                    developerMode = false,
                    supportsPrivacyMode = true
                ),
                GameSettingsActions(), {}, {},
            )
        }
    }
}

@Composable
private fun CardTextSymbolSettings(
    settings: GameSettingsState,
    settingsActions: GameSettingsActions,
) {
    ExpandableSymbolSettings(
        label = stringResource(R.string.deleted_card_texts),
        summary = stringResource(settings.deletedCardTextsMode.displayNameRes()),
        marker = CardTextMarker.BROKEN_HEART,
    ) {
        GeloeschteKartentexteModus.entries.forEach { mode ->
            SettingsRadioOption(
                label = stringResource(mode.displayNameRes()),
                selected = settings.deletedCardTextsMode == mode,
                onClick = { settingsActions.onDeletedCardTextsModeChanged(mode) },
            )
        }
    }
    HorizontalDivider()
    ExpandableSymbolSettings(
        label = stringResource(R.string.favorites),
        summary = stringResource(settings.favoritesMode.displayNameRes()),
        marker = CardTextMarker.STAR,
    ) {
        FavoritenModus.entries.forEach { mode ->
            SettingsRadioOption(
                label = stringResource(mode.displayNameRes()),
                selected = settings.favoritesMode == mode,
                onClick = { settingsActions.onFavoritesModeChanged(mode) },
            )
        }
    }
    HorizontalDivider()
    EditedCardTextSettings(settings, settingsActions)
}

@Composable
private fun EditedCardTextSettings(
    settings: GameSettingsState,
    settingsActions: GameSettingsActions,
) {
    ExpandableSymbolSettings(
        label = stringResource(R.string.edited_card_texts),
        summary = if (settings.developerMode) {
            stringResource(settings.editedCardTextsMode.displayNameRes())
        } else {
            stringResource(R.string.custom_translation_actions)
        },
        marker = CardTextMarker.PENCIL,
    ) {
        if (settings.developerMode) {
            BearbeiteteKartentexteModus.entries.forEach { mode ->
                SettingsRadioOption(
                    label = stringResource(mode.displayNameRes()),
                    selected = settings.editedCardTextsMode == mode,
                    onClick = { settingsActions.onEditedCardTextsModeChanged(mode) },
                )
            }
        }
        CustomTranslationSettings(
            allGames = false,
            actions = settingsActions.customTranslationActions
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardTextSymbolSettingsPreview() {
    ImpulseTheme {
        Column {
            CardTextSymbolSettings(
                settings = GameSettingsState(
                    textsPerCard = 1,
                    defaultTextsPerCard = 1,
                    developerMode = true
                ),
                settingsActions = GameSettingsActions(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditedCardTextSettingsPreview() {
    ImpulseTheme {
        EditedCardTextSettings(
            settings = GameSettingsState(
                textsPerCard = 1,
                defaultTextsPerCard = 1,
                developerMode = true
            ),
            settingsActions = GameSettingsActions(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameSettingsDialogPreview() {
    ImpulseTheme {
        GameSettingsDialog(
            settings = GameSettingsState(
                textsPerCard = 3,
                defaultTextsPerCard = 2,
                developerMode = true,
            ),
            settingsActions = GameSettingsActions(
                onResetSeenCards = {},
                onResetAllCards = {},
                onTextsPerCardChanged = {},
                onResetTextsPerCard = {},
            ),
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
        TextButton(onClick = onInfoClick) { Text(stringResource(R.string.symbol_information)) }
    }
}

@Composable
internal fun SettingsRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsRadioOptionPreview() {
    ImpulseTheme {
        SettingsRadioOption(
            label = stringResource(R.string.preview_unconsidered),
            selected = true,
            onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsActionRowPreview() {
    ImpulseTheme {
        SettingsActionRow(
            label = stringResource(R.string.preview_action),
            onClick = {},
            onInfoClick = {})
    }
}

@Composable
private fun SettingsInfoDialog(
    text: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.information)) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) } },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsInfoDialogPreview() {
    ImpulseTheme {
        SettingsInfoDialog(
            text = stringResource(R.string.reset_seen_info),
            onDismiss = {})
    }
}

private const val MIN_TEXTS_PER_CARD = 1
private const val MAX_TEXTS_PER_CARD = 5
