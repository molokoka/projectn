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
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.exit
import projectn.composeapp.generated.resources.flip_board
import projectn.composeapp.generated.resources.reset

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
            onSquareClicked = viewModel::onSquareClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
                .horizontalScroll(rememberScrollState())
        )

        AnalyticsView(
            tree = state.tree,
            moves = state.moves,
            onNodeSelected = viewModel::onAnalyticsNodeSelected,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(uiConfig.squareSize * BOARD_SIZE)
                .height(260.dp)
        )

        BottomBar(
            onFlipBoard = viewModel::flipBoard,
            onReset = viewModel::reset,
            onBackToInit = onBackToInit
        )
    }
}

@Composable
private fun BottomBar(onFlipBoard: () -> Unit, onReset: () -> Unit, onBackToInit: () -> Unit) {
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
