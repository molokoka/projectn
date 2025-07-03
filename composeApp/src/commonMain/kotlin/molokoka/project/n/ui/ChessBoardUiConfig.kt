package molokoka.project.n.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChessBoardUiConfig(
    val squareSize: Dp = 40.dp,
    val lightSquareColor: Color = Color(0xFFF0D9B5),
    val darkSquareColor: Color = Color(0xFFB58863),
    val conflictHighlightColor: Color = Color.Red.copy(alpha = 0.7f),
    val coordinateTextStyle: TextStyle = TextStyle(
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.6f)
    ),
    val queenTextStyle: TextStyle = TextStyle(
        fontSize = (squareSize.value * 0.6f).sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    ),
    val conflictingQueenTextStyle: TextStyle = TextStyle(
        fontSize = (squareSize.value * 0.6f).sp,
        fontWeight = FontWeight.Bold,
        color = Color.Red.copy(alpha = 0.7f)
    )
)

fun ChessBoardUiConfig.withConflictHighlight(baseColor: Color): Color {
    return baseColor.copy(alpha = 0.7f).compositeOver(conflictHighlightColor)
}

val LocalChessBoardUiConfig = staticCompositionLocalOf { ChessBoardUiConfig() }

@Composable
fun ChessBoardUiConfigProvider(
    config: ChessBoardUiConfig = ChessBoardUiConfig(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalChessBoardUiConfig provides config) {
        content()
    }
}

@Composable
fun chessBoardUiConfig(): ChessBoardUiConfig = LocalChessBoardUiConfig.current