package molokoka.project.n.analysis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.Move
import molokoka.project.n.ui.ScrollableViewPan
import molokoka.project.n.ui.panned
import molokoka.project.n.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.current_moves
import projectn.composeapp.generated.resources.moves_tree

@Composable
fun AnalysisView(
    viewState: AnalysisViewState,
    onNodeSelected: (List<Move>) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens

    val listState = rememberLazyListState()
    val pathScroll = rememberScrollState()
    val selectedRow = viewState.selectedRow

    LaunchedEffect(selectedRow) {
        if (selectedRow != null) listState.animateScrollToCentre(selectedRow)
    }

    LaunchedEffect(viewState.currentMoves, pathScroll.maxValue) {
        pathScroll.animateScrollTo(pathScroll.maxValue)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing),
        modifier = modifier
    ) {
        SectionTitle(stringResource(Res.string.current_moves, viewState.moveCount))

        BasicText(
            text = viewState.currentMoves,
            style = AppTheme.typography.move,
            softWrap = false,
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.headerBackground)
                .horizontalScroll(pathScroll)
                .padding(dimens.currentMovesPadding)
        )

        SectionTitle(stringResource(Res.string.moves_tree))

        val scrollableViewPan = remember { ScrollableViewPan() }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .scrollable(
                    orientation = Orientation.Horizontal,
                    state = rememberScrollableState(scrollableViewPan::drag)
                )
        ) {
            items(viewState.rows) { row ->
                MoveRow(
                    label = row.label,
                    background = if (row.side == null) {
                        colors.headerBackground
                    } else {
                        colors.moveRow(row.side)
                    },
                    isSelected = row.isSelected,
                    scrollableViewPan = scrollableViewPan,
                    onClick = { onNodeSelected(row.path) }
                )
            }
        }
    }
}

private suspend fun LazyListState.animateScrollToCentre(row: Int) {
    val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
    val rowHeight = layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0

    animateScrollToItem(row, -(viewport - rowHeight) / 2)
}

@Composable
private fun SectionTitle(text: String) {
    BasicText(
        text = text,
        style = AppTheme.typography.sectionTitle
    )
}

@Composable
private fun MoveRow(
    label: String,
    background: Color,
    isSelected: Boolean,
    scrollableViewPan: ScrollableViewPan,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val dimens = AppTheme.dimens
    val selectedBorder = Modifier.border(
        dimens.selectedMoveRowBorder,
        colors.selectedMoveRow
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .then(if (isSelected) selectedBorder else Modifier)
            .clickable { onClick() }
            .clipToBounds()
            .padding(
                horizontal = dimens.moveRowHorizontalPadding,
                vertical = dimens.moveRowVerticalPadding
            )
    ) {
        BasicText(
            text = label,
            style = AppTheme.typography.move,
            softWrap = false,
            modifier = Modifier.panned(scrollableViewPan)
        )
    }
}

private val ViewWidth = 360.dp
private val ViewHeight = 320.dp

@Composable
private fun AnalysisViewPreview(viewState: AnalysisViewState = PreviewEmptyViewState) {
    AppTheme {
        Box(
            modifier = Modifier
                .width(ViewWidth)
                .height(ViewHeight)
        ) {
            AnalysisView(
                viewState = viewState,
                onNodeSelected = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun AnalysisViewEmptyPreview() {
    AnalysisViewPreview()
}

@Preview
@Composable
fun AnalysisViewLinePreview() {
    AnalysisViewPreview(viewState = PreviewLineViewState)
}

@Preview
@Composable
fun AnalysisViewStartSelectedPreview() {
    AnalysisViewPreview(viewState = PreviewStartSelectedViewState)
}

@Preview
@Composable
fun AnalysisViewEvaluatedPreview() {
    AnalysisViewPreview(viewState = PreviewEvaluatedLineViewState)
}

@Preview
@Composable
fun AnalysisViewBranchingPreview() {
    AnalysisViewPreview(viewState = PreviewBranchedLineViewState)
}
