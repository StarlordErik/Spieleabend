package de.kaserik.impulse.frontend.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

internal const val MIN_GAME_PLAYERS = 2
internal const val MAX_GAME_PLAYERS = 10

@Composable
internal fun PlayerCountSetup(
    onStart: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minimum: Int = MIN_GAME_PLAYERS,
) {
    var selectedCount by rememberSaveable(minimum) { mutableStateOf<Int?>(null) }
    Box(modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = 360.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                stringResource(R.string.player_count_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Column(
                Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                (minimum..MAX_GAME_PLAYERS).chunked(PLAYER_COUNT_COLUMNS).forEach { counts ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        counts.forEach { count ->
                            val selected = selectedCount == count
                            Surface(
                                modifier = Modifier.weight(1f).heightIn(min = 64.dp)
                                    .selectable(selected, role = Role.RadioButton) { selectedCount = count },
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainer,
                                border = BorderStroke(
                                    if (selected) 2.dp else 1.dp,
                                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                ),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(count.toString(), style = MaterialTheme.typography.headlineSmall)
                                }
                            }
                        }
                        repeat(PLAYER_COUNT_COLUMNS - counts.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
            Button(
                onClick = { selectedCount?.let(onStart) },
                enabled = selectedCount != null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            ) { Text(stringResource(R.string.start_game)) }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 820)
@Composable
private fun PlayerCountSetupPreview() {
    ImpulseTheme { PlayerCountSetup({}) }
}

private const val PLAYER_COUNT_COLUMNS = 3
