package molokoka.project.n

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
    val conflictHighlightColor: Color = Color.Companion.Red.copy(alpha = 0.7f),
    val coordinateTextStyle: TextStyle = TextStyle(
        fontSize = 8.sp,
        fontWeight = FontWeight.Companion.Bold,
        color = Color.Companion.Black.copy(alpha = 0.6f)
    ),
    val queenTextStyle: TextStyle = TextStyle(
        fontSize = (squareSize.value * 0.6f).sp,
        fontWeight = FontWeight.Companion.Bold,
        color = Color.Companion.Black
    ),
    val conflictingQueenTextStyle: TextStyle = TextStyle(
        fontSize = (squareSize.value * 0.6f).sp,
        fontWeight = FontWeight.Companion.Bold,
        color = Color.Companion.Red.copy(alpha = 0.7f)
    )
)