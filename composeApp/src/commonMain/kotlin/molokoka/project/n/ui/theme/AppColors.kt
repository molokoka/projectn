package molokoka.project.n.ui.theme

import androidx.compose.ui.graphics.Color
import molokoka.project.n.domain.Side

data class AppColors(
    val background: Color = Color.White,
    val lightSquare: Color = Color(0xFFF0D9B5),
    val darkSquare: Color = Color(0xFFB58863),
    val selectedSquare: Color = Color(0xFF7FB069),
    val boardCoordinates: Color = Color.Black.copy(alpha = 0.6f),
    val title: Color = Color.Black,
    val action: Color = Color.Black,
    val sectionTitle: Color = Color(0xFF757575),
    val move: Color = Color.Black,
    val headerBackground: Color = Color(0xFFE0E0E0),
    val whiteMoveRow: Color = Color.White,
    val blackMoveRow: Color = Color(0xFFF0F0F0),
    val selectedMoveRow: Color = Color.Black
) {
    fun moveRow(side: Side): Color =
        if (side == Side.WHITE) whiteMoveRow else blackMoveRow
}
