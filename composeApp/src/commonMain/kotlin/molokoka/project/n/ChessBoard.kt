package molokoka.project.n

import androidx.compose.foundation.background
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

@Composable
fun ChessBoard (
    modifier: Modifier = Modifier,
    boardSize: Int = 8
) {
    ChessField(
        modifier = modifier,
        boardSize = boardSize,
        squareSize = 40.dp
    )
}


@Composable
fun ChessField(
    modifier: Modifier = Modifier,
    boardSize: Int = 8,
    squareSize: Dp = 40.dp
) {
    val lightSquareColor = Color(0xFFF0D9B5)
    val darkSquareColor = Color(0xFFB58863)
    val coordinateTextStyle = TextStyle(
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.6f)
    )

    Column(modifier = modifier) {
        repeat(boardSize) { row ->
            Row {
                repeat(boardSize) { col ->
                    val isLightSquare = (row + col) % 2 == 0
                    val squareColor = if (isLightSquare) lightSquareColor else darkSquareColor
                    val isLeftEdge = col == 0
                    val isBottomEdge = row == boardSize - 1

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .background(squareColor)
                    ) {
                        if (isLeftEdge) {
                            BasicText(
                                text = (boardSize - row).toString(),
                                style = coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(1.dp)
                            )
                        }
                        if (isBottomEdge) {
                            BasicText(
                                text = ('a' + col).toString(),
                                style = coordinateTextStyle,
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
