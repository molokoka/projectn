package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChessBoardConfig(
    val squareSize: Dp = 40.dp,
    val lightSquareColor: Color = Color(0xFFF0D9B5),
    val darkSquareColor: Color = Color(0xFFB58863),
    val coordinateTextStyle: TextStyle = TextStyle(
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.6f)
    ),
    val queenTextStyle: TextStyle = TextStyle(
        fontSize = (squareSize.value * 0.6f).sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
)

data class ChessBoardState(
    val boardSize: Int = 8,
    val queensPositions: Set<Pair<Int, Int>> = emptySet()
) {
    fun toggleQueen(row: Int, col: Int): ChessBoardState {
        val position = Pair(row, col)
        return if (queensPositions.contains(position)) {
            copy(queensPositions = queensPositions - position)
        } else {
            copy(queensPositions = queensPositions + position)
        }
    }

    fun hasQueen(row: Int, col: Int): Boolean = queensPositions.contains(Pair(row, col))
}

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    config: ChessBoardConfig = ChessBoardConfig(),
    boardState: ChessBoardState = ChessBoardState(),
    onSquareClicked: (row: Int, col: Int) -> Unit
) {

    ChessField(
        modifier = modifier,
        boardState = boardState,
        onSquareClicked = onSquareClicked,
        config = config
    )
}


@Composable
private fun ChessField(
    modifier: Modifier = Modifier,
    boardState: ChessBoardState,
    onSquareClicked: (row: Int, col: Int) -> Unit,
    config: ChessBoardConfig = ChessBoardConfig()
) {

    Column(modifier = modifier) {
        repeat(boardState.boardSize) { row ->
            Row {
                repeat(boardState.boardSize) { col ->
                    val isLightSquare = (row + col) % 2 == 0
                    val squareColor = if (isLightSquare) config.lightSquareColor else config.darkSquareColor
                    val isLeftEdge = col == 0
                    val isBottomEdge = row == boardState.boardSize - 1
                    val hasQueen = boardState.hasQueen(row, col)

                    Box(
                        modifier = Modifier
                            .size(config.squareSize)
                            .background(squareColor)
                            .clickable {
                                onSquareClicked(row, col)
                            }
                    ) {
                        Queen(
                            modifier = Modifier.align(Alignment.Center),
                            show = hasQueen,
                            queenTextStyle = config.queenTextStyle
                        )


                        if (isLeftEdge) {
                            BasicText(
                                text = (boardState.boardSize - row).toString(),
                                style = config.coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(1.dp)
                            )
                        }
                        if (isBottomEdge) {
                            BasicText(
                                text = ('a' + col).toString(),
                                style = config.coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Queen(
    modifier: Modifier = Modifier,
    show: Boolean,
    queenTextStyle: TextStyle,
) {
    if (show) {
        BasicText(
            text = "♛",
            style = queenTextStyle,
            modifier = modifier,
        )
    }
}
