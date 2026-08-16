package molokoka.project.n.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molokoka.project.n.domain.Side

data class AnalysisUiConfig(
    val mutedTextColor: Color = Color(0xFF757575),
    val headerColor: Color = Color(0xFFE0E0E0),
    val whiteMoveColor: Color = Color.White,
    val blackMoveColor: Color = Color(0xFFF0F0F0),
    val selectedRowColor: Color = Color.Black,
    val selectedRowBorder: Dp = 2.dp
) {
    fun moveColor(side: Side): Color =
        if (side == Side.WHITE) whiteMoveColor else blackMoveColor
}

data class ChessBoardUiConfig(
    val squareSize: Dp = 40.dp,
    val lightSquareColor: Color = Color(0xFFF0D9B5),
    val darkSquareColor: Color = Color(0xFFB58863),
    val coordinateTextStyle: TextStyle = TextStyle(
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.6f)
    ),
    val pieceTextStyle: TextStyle = TextStyle(
        fontSize = 30.sp,
        color = Color.Black
    ),
    val selectedSquareColor: Color = Color(0xFF7FB069),
    val analysis: AnalysisUiConfig = AnalysisUiConfig()
)

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
