package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.chess.ChessCoordinates
import molokoka.project.n.ui.BoardOrientation
import molokoka.project.n.ui.ChessBoard
import molokoka.project.n.ui.PixelatedText
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.*

@Composable
fun AnalysisScreen(
    onSquareClicked: (ChessCoordinates) -> Unit,
    onBackToInit: () -> Unit,
    onReset: () -> Unit
) {
    var orientation by remember { mutableStateOf(BoardOrientation.WHITE) }

    Column {
        ChessBoard(
            orientation = orientation,
            onSquareClicked = onSquareClicked,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )

        BottomBar(
            onFlipBoard = {
                orientation = when (orientation) {
                    BoardOrientation.WHITE -> BoardOrientation.BLACK
                    BoardOrientation.BLACK -> BoardOrientation.WHITE
                }
            },
            onReset = onReset,
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
        PixelatedText(
            text = stringResource(Res.string.flip_board),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onFlipBoard() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelatedText(
            text = stringResource(Res.string.reset),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onReset() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelatedText(
            text = stringResource(Res.string.exit),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(8.dp)
        )
    }
}
