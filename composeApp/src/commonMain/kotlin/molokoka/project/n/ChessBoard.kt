package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    boardSize: Int = 8
) {
    val lightSquareColor = Color(0xFFF0D9B5)
    val darkSquareColor = Color(0xFFB58863)
    
    Column(
        modifier = modifier
    ) {
        repeat(boardSize) { row ->
            Row {
                repeat(boardSize) { col ->
                    val isLightSquare = (row + col) % 2 == 0
                    val squareColor = if (isLightSquare) lightSquareColor else darkSquareColor
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(squareColor)
                    )
                }
            }
        }
    }
}