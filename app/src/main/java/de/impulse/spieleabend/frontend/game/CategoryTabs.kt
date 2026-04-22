@file:Suppress("MagicNumber")

package de.impulse.spieleabend.frontend.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.impulse.spieleabend.frontend.theme.SpieleabendTheme

@Composable
internal fun CategoryTabs(
    kategorien: List<GameKategorieUiModel>,
    modifier: Modifier = Modifier,
) {
    val leftTabs = kategorien.filterIndexed { index, _ -> index % 2 == 0 }
    val rightTabs = kategorien.filterIndexed { index, _ -> index % 2 == 1 }

    Box(modifier = modifier) {
        CategoryTabColumn(
            tabs = leftTabs,
            side = CategoryTabSide.Left,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        CategoryTabColumn(
            tabs = rightTabs,
            side = CategoryTabSide.Right,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTabsPreview() {
    SpieleabendTheme {
        CategoryTabs(
            kategorien = PreviewUiState.kategorien,
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
        )
    }
}

@Composable
private fun CategoryTabColumn(
    tabs: List<GameKategorieUiModel>,
    side: CategoryTabSide,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tabs.forEachIndexed { index, tab ->
            CategoryTab(
                tab = tab,
                side = side,
                color = CategoryTabColors[index % CategoryTabColors.size],
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTabColumnPreview() {
    SpieleabendTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CategoryTabColumn(
                tabs = PreviewUiState.kategorien.take(2),
                side = CategoryTabSide.Left,
            )
            CategoryTabColumn(
                tabs = PreviewUiState.kategorien.drop(2),
                side = CategoryTabSide.Right,
            )
        }
    }
}

@Composable
private fun CategoryTab(
    tab: GameKategorieUiModel,
    side: CategoryTabSide,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(CategoryTabWidth)
            .height(CategoryTabHeight)
            .clip(categoryTabShape(side))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tab.name.uppercase(),
            modifier = Modifier
                .width(CategoryTabLabelWidth)
                .graphicsLayer(rotationZ = side.labelRotation),
            color = CategoryTabContentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTabPreview() {
    SpieleabendTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CategoryTab(
                tab = PreviewUiState.kategorien.first(),
                side = CategoryTabSide.Left,
                color = CategoryTabColors.first(),
            )
            CategoryTab(
                tab = PreviewUiState.kategorien.last(),
                side = CategoryTabSide.Right,
                color = CategoryTabColors.last(),
            )
        }
    }
}

private fun categoryTabShape(side: CategoryTabSide): Shape =
    when (side) {
        CategoryTabSide.Left -> RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
        CategoryTabSide.Right -> RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
    }

private enum class CategoryTabSide(
    val labelRotation: Float,
) {
    Left(labelRotation = -90f),
    Right(labelRotation = 90f),
}

private val CategoryTabContentColor = Color.White
private val CategoryTabWidth = 36.dp
private val CategoryTabHeight = 128.dp
private val CategoryTabLabelWidth = 108.dp

private val CategoryTabColors = listOf(
    Color(0xFF2A7F62),
    Color(0xFFC84C31),
    Color(0xFF276A8A),
    Color(0xFF8B5A96),
)
