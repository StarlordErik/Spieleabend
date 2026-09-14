package de.kaserik.impulse.frontend.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.kaserik.impulse.R
import de.kaserik.impulse.frontend.theme.ImpulseTheme

@Composable
internal fun PrivacyResults(
    session: PrivacySession,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier,
    nextRoundEnabled: Boolean = true,
) {
    Column(
        modifier = Modifier.widthIn(max = 440.dp).then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.privacy_ranking),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.privacy_total_after_round, session.roundNumber),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            stringResource(
                when (session.yesCount) {
                    0 -> R.string.privacy_all_no_rule
                    session.playerCount -> R.string.privacy_all_yes_rule
                    else -> R.string.privacy_points_rule
                },
            ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                session.ranking,
                key = { _, standing -> standing.player.id }) { index, standing ->
                PrivacyRankingRow(standing, index)
            }
        }
        Button(
            onClick = onNextRound, enabled = nextRoundEnabled,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).heightIn(min = 52.dp)
        ) {
            Text(stringResource(R.string.next_card))
        }
    }
}

@Composable
private fun PrivacyRankingRow(standing: PrivacyRanking, index: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        if (visible) 1f else 0f,
        tween(350, delayMillis = index * 65),
        label = "privacyStanding"
    )
    val points by animateIntAsState(
        if (visible) standing.player.points else standing.player.points - standing.roundPoints,
        tween(650, delayMillis = index * 65), label = "privacyPoints",
    )
    Surface(
        modifier = Modifier.fillMaxWidth()
            .graphicsLayer { this.alpha = alpha; translationY = (1f - alpha) * 18.dp.toPx() },
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        border = if (standing.rank == 1) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ) else null,
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.privacy_rank, standing.rank),
                modifier = Modifier.width(28.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    standing.player.name, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium
                )
                Text(
                    pluralStringResource(
                        R.plurals.privacy_player_result, standing.roundPoints,
                        standing.prediction, standing.roundPoints
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    points.toString(),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 364, heightDp = 520)
@Composable
private fun PrivacyResultsPreview() {
    ImpulseTheme { PrivacyResults(remember { previewPrivacyResults() }, {}) }
}

@Preview(showBackground = true, widthDp = 364)
@Composable
private fun PrivacyRankingRowPreview() {
    ImpulseTheme { PrivacyRankingRow(remember { previewPrivacyResults().ranking.first() }, 0) }
}

internal fun previewPrivacyResults(): PrivacySession = previewPrivacySession(2).apply {
    draft.chooseVote(true)
    draft.choosePrediction(2)
    nextPlayer()
    draft.updateName("Sam")
    draft.chooseVote(false)
    draft.choosePrediction(1)
    reveal()
    completeReveal()
}
