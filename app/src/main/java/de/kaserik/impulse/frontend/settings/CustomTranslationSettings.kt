package de.kaserik.impulse.frontend.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun CustomTranslationSettings(
    allGames: Boolean,
    actions: CustomTranslationActions,
) {
    var showImport by rememberSaveable { mutableStateOf(false) }
    var showReset by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { showImport = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.apply_erik_translations))
        }
        TextButton(onClick = { showReset = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.reset_custom_translations))
        }
    }
    if (showImport) {
        ErikTranslationDialog(
            allGames = allGames,
            onApply = { overwrite ->
                actions.onApplyErikTranslations(overwrite)
                showImport = false
            },
            onDismiss = { showImport = false },
        )
    }
    if (showReset) {
        ResetCustomTranslationsDialog(
            allGames = allGames,
            onReset = {
                actions.onResetCustomTranslations()
                showReset = false
            },
            onDismiss = { showReset = false },
        )
    }
}

@Composable
private fun ErikTranslationDialog(
    allGames: Boolean,
    onApply: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var overwriteExisting by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.apply_erik_translations)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(stringResource(if (allGames) R.string.erik_import_all_info else R.string.erik_import_game_info))
                SettingsRadioOption(
                    label = stringResource(R.string.erik_import_missing_only),
                    selected = !overwriteExisting,
                    onClick = { overwriteExisting = false },
                )
                SettingsRadioOption(
                    label = stringResource(R.string.erik_import_overwrite),
                    selected = overwriteExisting,
                    onClick = { overwriteExisting = true },
                )
                Text(stringResource(R.string.erik_import_german_info))
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(overwriteExisting) }) {
                Text(stringResource(R.string.apply_translations))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@Composable
private fun ResetCustomTranslationsDialog(
    allGames: Boolean,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_custom_translations)) },
        text = {
            Text(stringResource(if (allGames) R.string.reset_custom_all_info else R.string.reset_custom_game_info))
        },
        confirmButton = {
            TextButton(onClick = onReset) { Text(stringResource(R.string.reset_translations)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTranslationSettingsPreview() {
    ImpulseTheme { CustomTranslationSettings(allGames = false, actions = CustomTranslationActions()) }
}

@Preview(showBackground = true)
@Composable
private fun ErikTranslationDialogPreview() {
    ImpulseTheme { ErikTranslationDialog(allGames = true, onApply = {}, onDismiss = {}) }
}

@Preview(showBackground = true)
@Composable
private fun ResetCustomTranslationsDialogPreview() {
    ImpulseTheme { ResetCustomTranslationsDialog(allGames = true, onReset = {}, onDismiss = {}) }
}
