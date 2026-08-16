package molokoka.project.n.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molokoka.project.n.domain.Move
import molokoka.project.n.analysis.AnalysisTree
import molokoka.project.n.domain.sideToMove
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.current_moves
import projectn.composeapp.generated.resources.moves_tree
import projectn.composeapp.generated.resources.start

private const val DEPTH_MARKER = "."

@Composable
fun AnalysisView(
    tree: AnalysisTree,
    moves: List<Move>,
    onNodeSelected: (List<Move>) -> Unit,
    modifier: Modifier = Modifier
) {
    val analysisUiConfig = chessBoardUiConfig().analysis
    val paths = tree.paths()

    val currentMoves = moves.indices.joinToString(" ") { moveCount ->
        tree.moveWithEvaluation(moves.take(moveCount + 1))
    }

    val listState = rememberLazyListState()
    val pathScroll = rememberScrollState()
    val selectedRow = if (moves.isEmpty()) 0 else paths.indexOf(moves) + 1

    LaunchedEffect(selectedRow) {
        if (selectedRow >= 0) listState.animateScrollToCentre(selectedRow)
    }

    LaunchedEffect(moves, pathScroll.maxValue) {
        pathScroll.animateScrollTo(pathScroll.maxValue)
    }

    Column(modifier = modifier) {
        SectionTitle(stringResource(Res.string.current_moves, moves.size))

        BasicText(
            text = currentMoves,
            style = moveTextStyle(),
            softWrap = false,
            modifier = Modifier
                .fillMaxWidth()
                .background(analysisUiConfig.headerColor)
                .horizontalScroll(pathScroll)
                .padding(6.dp)
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
            item {
                MoveRow(
                    label = stringResource(Res.string.start),
                    background = analysisUiConfig.headerColor,
                    isSelected = moves.isEmpty(),
                    scrollableViewPan = scrollableViewPan,
                    onClick = { onNodeSelected(emptyList()) }
                )
            }

            items(paths) { path ->
                MoveRow(
                    label = DEPTH_MARKER.repeat(path.size) + " " + tree.moveWithEvaluation(path),
                    background = analysisUiConfig.moveColor(sideToMove(path.size - 1)),
                    isSelected = path == moves,
                    scrollableViewPan = scrollableViewPan,
                    onClick = { onNodeSelected(path) }
                )
            }
        }
    }
}

private fun AnalysisTree.moveWithEvaluation(path: List<Move>): String {
    val move = path.last()
    val moveEvaluation = evaluationAt(path)

    return if (moveEvaluation == null) "$move" else "$move$moveEvaluation"
}

private suspend fun LazyListState.animateScrollToCentre(row: Int) {
    val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
    val rowHeight = layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0

    animateScrollToItem(row, -(viewport - rowHeight) / 2)
}

@Composable
private fun SectionTitle(text: String) {
    val analysisUiConfig = chessBoardUiConfig().analysis

    BasicText(
        text = text,
        style = TextStyle(
            fontFamily = karmaticArcade(),
            fontSize = 12.sp,
            color = analysisUiConfig.mutedTextColor
        ),
        modifier = Modifier.padding(top = 8.dp, bottom = 3.dp)
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
    val analysisUiConfig = chessBoardUiConfig().analysis
    val selectedBorder = Modifier.border(
        analysisUiConfig.selectedRowBorder,
        analysisUiConfig.selectedRowColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .then(if (isSelected) selectedBorder else Modifier)
            .clickable { onClick() }
            .clipToBounds()
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        BasicText(
            text = label,
            style = moveTextStyle(),
            softWrap = false,
            modifier = Modifier.panned(scrollableViewPan)
        )
    }
}

@Composable
private fun moveTextStyle() = TextStyle(
    fontFamily = byteBounce(),
    fontSize = 20.sp,
    color = Color.Black
)
