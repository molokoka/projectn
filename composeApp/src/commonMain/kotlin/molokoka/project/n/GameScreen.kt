package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.ui.ChessBoard
import molokoka.project.n.ui.ChessBoardState
import molokoka.project.n.domain.chess.BOARD_SIZE
import molokoka.project.n.domain.chess.ChessCoordinates
import molokoka.project.n.ui.PixelatedText
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.*

@Composable
fun GameScreen(
    boardState: ChessBoardState,
    onSquareClicked: (ChessCoordinates) -> Unit,
    onBackToInit: () -> Unit,
    onRestart: () -> Unit
) {
    Column {
        GameInfoHeader(boardState = boardState)

        Spacer(modifier = Modifier.height(8.dp))

        ChessBoard(
            boardState = boardState,
            onSquareClicked = onSquareClicked,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )

        BottomBar(
            onRestart = onRestart,
            onBackToInit = onBackToInit
        )
    }
}

@Composable
private fun GameInfoHeader(boardState: ChessBoardState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelatedText(
            text = stringResource(
                Res.string.queens_left,
                boardState.queensPositions.size,
                BOARD_SIZE
            ),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
private fun BottomBar(onRestart: () -> Unit, onBackToInit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelatedText(
            text = stringResource(Res.string.restart),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onRestart() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelatedText(
            text = stringResource(Res.string.exit_game),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(8.dp)
        )
    }
}
