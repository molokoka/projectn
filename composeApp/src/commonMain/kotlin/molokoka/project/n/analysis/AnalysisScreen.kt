package molokoka.project.n.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import molokoka.project.n.domain.BOARD_SIZE
import molokoka.project.n.ui.ChessBoard
import molokoka.project.n.ui.AnalyticsView
import molokoka.project.n.ui.chessBoardUiConfig
import molokoka.project.n.ui.karmaticArcade
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import molokoka.project.n.analysis.AnalysisIntent.OnSquareClick
import molokoka.project.n.analysis.AnalysisIntent.FlipBoard
import molokoka.project.n.analysis.AnalysisIntent.RequestComputerMove
import molokoka.project.n.analysis.AnalysisIntent.Reset
import molokoka.project.n.analysis.AnalysisIntent.SelectNode
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.computer_move
import projectn.composeapp.generated.resources.exit
import projectn.composeapp.generated.resources.flip_board
import projectn.composeapp.generated.resources.reset
import projectn.composeapp.generated.resources.thinking

@Composable
fun AnalysisScreen(
    onBackToInit: () -> Unit,
    viewModel: AnalysisViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiConfig = chessBoardUiConfig()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ChessBoard(
            position = state.position,
            selected = state.selected,
            orientation = state.orientation,
            onSquareClicked = { viewModel.onIntent(OnSquareClick(it)) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
                .horizontalScroll(rememberScrollState())
        )

        AnalyticsView(
            tree = state.tree,
            moves = state.moves,
            onNodeSelected = { viewModel.onIntent(SelectNode(it)) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(uiConfig.squareSize * BOARD_SIZE)
                .height(260.dp)
        )

        BottomBar(
            computerMovePending = state.computerMovePending,
            onComputerMove = { viewModel.onIntent(RequestComputerMove) },
            onFlipBoard = { viewModel.onIntent(FlipBoard) },
            onReset = { viewModel.onIntent(Reset) },
            onBackToInit = onBackToInit
        )
    }
}

@Composable
private fun BottomBar(
    computerMovePending: Boolean,
    onComputerMove: () -> Unit,
    onFlipBoard: () -> Unit,
    onReset: () -> Unit,
    onBackToInit: () -> Unit
) {
    val uiConfig = chessBoardUiConfig()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val labelStyle = TextStyle(
            fontFamily = karmaticArcade(),
            fontSize = 16.sp,
            color = Color.Blue
        )

        BasicText(
            text = if (computerMovePending) {
                stringResource(Res.string.thinking)
            } else {
                stringResource(Res.string.computer_move)
            },
            style = labelStyle.copy(
                color = if (computerMovePending) uiConfig.analytics.mutedTextColor else Color.Blue
            ),
            modifier = Modifier
                .clickable(enabled = !computerMovePending) { onComputerMove() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = stringResource(Res.string.flip_board),
            style = labelStyle,
            modifier = Modifier
                .clickable { onFlipBoard() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = stringResource(Res.string.reset),
            style = labelStyle,
            modifier = Modifier
                .clickable { onReset() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = stringResource(Res.string.exit),
            style = labelStyle,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(8.dp)
        )
    }
}
