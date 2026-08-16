package molokoka.project.n.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import molokoka.project.n.analysis.AnalysisIntent.FlipBoard
import molokoka.project.n.analysis.AnalysisIntent.OnSquareClick
import molokoka.project.n.analysis.AnalysisIntent.RequestComputerMove
import molokoka.project.n.analysis.AnalysisIntent.RequestMovesEvaluation
import molokoka.project.n.analysis.AnalysisIntent.Reset
import molokoka.project.n.analysis.AnalysisIntent.SelectNode
import molokoka.project.n.computer_move.DelayedRandomComputerMoveSource
import molokoka.project.n.move_evaluation.DelayedRandomMoveEvaluationSource
import molokoka.project.n.ui.ChessBoard
import molokoka.project.n.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.analysis
import projectn.composeapp.generated.resources.analysis_board
import projectn.composeapp.generated.resources.computer_move
import projectn.composeapp.generated.resources.flip_board
import projectn.composeapp.generated.resources.loading
import projectn.composeapp.generated.resources.reset

private val MinContentHeight = 640.dp
private const val BoardHeightFraction = 0.420f

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimens = AppTheme.dimens

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentHeight = maxOf(maxHeight, MinContentHeight)
        val contentWidth = maxWidth - dimens.screenPadding * 2
        val boardSize = minOf(contentWidth, contentHeight * BoardHeightFraction)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimens.blockSpacing),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .height(contentHeight)
                .fillMaxWidth()
                .padding(dimens.screenPadding)
        ) {
            Title()

            ChessBoard(
                position = state.position,
                selected = state.selected,
                orientation = state.orientation,
                boardSize = boardSize,
                onSquareClicked = { viewModel.onIntent(OnSquareClick(it)) }
            )

            AnalysisView(
                tree = state.tree,
                moves = state.moves,
                onNodeSelected = { viewModel.onIntent(SelectNode(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            ActionBar(
                isComputerMovePending = state.isComputerMovePending,
                isMoveEvaluationPending = state.isMoveEvaluationPending,
                onComputerMove = { viewModel.onIntent(RequestComputerMove) },
                onAnalysis = { viewModel.onIntent(RequestMovesEvaluation) },
                onFlipBoard = { viewModel.onIntent(FlipBoard) },
                onReset = { viewModel.onIntent(Reset) }
            )
        }
    }
}

@Composable
private fun Title() {
    val typography = AppTheme.typography

    BasicText(
        text = stringResource(Res.string.analysis_board),
        style = typography.title,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            minFontSize = typography.titleMinFontSize,
            maxFontSize = typography.title.fontSize
        )
    )
}

@Composable
private fun ActionBar(
    isComputerMovePending: Boolean,
    isMoveEvaluationPending: Boolean,
    onComputerMove: () -> Unit,
    onAnalysis: () -> Unit,
    onFlipBoard: () -> Unit,
    onReset: () -> Unit
) {
    val loading = stringResource(Res.string.loading)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.actionSpacing)
    ) {
        Action(
            text = stringResource(Res.string.computer_move) +
                    if (isComputerMovePending) " $loading" else "",
            onClick = onComputerMove
        )
        Action(
            text = stringResource(Res.string.analysis) +
                    if (isMoveEvaluationPending) " $loading" else "",
            onClick = onAnalysis
        )
        Action(
            text = stringResource(Res.string.flip_board),
            onClick = onFlipBoard
        )
        Action(
            text = stringResource(Res.string.reset),
            onClick = onReset
        )
    }
}

@Composable
private fun Action(
    text: String,
    onClick: () -> Unit
) {
    BasicText(
        text = text,
        style = AppTheme.typography.action,
        modifier = Modifier
            .clickable { onClick() }
            .padding(AppTheme.dimens.actionPadding)
    )
}

// previews

private val PhoneWidth = 400.dp
private val PhoneHeight = 820.dp
private val ThinWidth = 300.dp
private val ShortHeight = 380.dp

@Composable
private fun AnalysisScreenPreview(
    width: Dp = PhoneWidth,
    height: Dp = PhoneHeight,
    analysis: AnalysisState = AnalysisState(),
    intents: List<AnalysisIntent> = emptyList()
) {
    val viewModel = remember {
        AnalysisViewModel(
            DelayedRandomComputerMoveSource(),
            DelayedRandomMoveEvaluationSource(),
            previewHandle(analysis)
        ).also { previewed -> intents.forEach(previewed::onIntent) }
    }

    AppTheme {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
        ) {
            AnalysisScreen(viewModel = viewModel)
        }
    }
}

@Preview
@Composable
fun AnalysisScreenInitialPreview() {
    AnalysisScreenPreview()
}

@Preview
@Composable
fun AnalysisScreenEvaluatedLinePreview() {
    AnalysisScreenPreview(
        analysis = PreviewEvaluatedLineWithLastMoveSelected
    )
}

@Preview
@Composable
fun AnalysisScreenComputerMovePendingPreview() {
    AnalysisScreenPreview(
        analysis = PreviewOpeningLineWithLastMoveSelected,
        intents = listOf(RequestComputerMove)
    )
}

@Preview
@Composable
fun AnalysisScreenEvaluationPendingPreview() {
    AnalysisScreenPreview(
        analysis = PreviewOpeningLineWithLastMoveSelected,
        intents = listOf(RequestMovesEvaluation)
    )
}

@Preview
@Composable
fun AnalysisScreenFlippedPreview() {
    AnalysisScreenPreview(
        analysis = PreviewOpeningLineWithLastMoveSelected,
        intents = listOf(FlipBoard)
    )
}

@Preview
@Composable
fun AnalysisScreenThinPreview() {
    AnalysisScreenPreview(
        width = ThinWidth,
        analysis = PreviewEvaluatedLineWithLastMoveSelected
    )
}

@Preview
@Composable
fun AnalysisScreenShortPreview() {
    AnalysisScreenPreview(
        height = ShortHeight,
        analysis = PreviewOpeningLineWithLastMoveSelected
    )
}
