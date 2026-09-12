package de.kaserik.impulse.frontend.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.game.CardTextMarker
import de.kaserik.impulse.frontend.game.CardTextMarkerIcon
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun ExpandableSymbolSettings(
    label: String,
    summary: String,
    marker: CardTextMarker,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expandedDescription = stringResource(if (expanded) R.string.expanded else R.string.collapsed)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(if (expanded) R.string.collapse else R.string.expand),
                ) { expanded = !expanded }
                .semantics { stateDescription = expandedDescription }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CardTextMarkerIcon(marker = marker, checked = false, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Text(summary, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                stringResource(if (expanded) R.string.symbol_collapse else R.string.symbol_expand),
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
        if (expanded) content()
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableSymbolSettingsPreview() {
    ImpulseTheme {
        ExpandableSymbolSettings(
            label = stringResource(R.string.favorites),
            summary = stringResource(R.string.mode_show_regardless),
            marker = CardTextMarker.STAR,
        ) { }
    }
}
