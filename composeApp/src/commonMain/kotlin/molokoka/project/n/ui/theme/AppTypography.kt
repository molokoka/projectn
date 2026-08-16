package molokoka.project.n.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class AppTypography(
    val title: TextStyle,
    val titleMinFontSize: TextUnit,
    val action: TextStyle,
    val sectionTitle: TextStyle,
    val move: TextStyle,
    val boardCoordinates: TextStyle
)

@Composable
fun defaultTypography(colors: AppColors = AppColors()) = AppTypography(
    title = TextStyle(
        fontFamily = karmaticArcade(),
        fontSize = 28.sp,
        color = colors.title,
        textAlign = TextAlign.Center
    ),
    titleMinFontSize = 14.sp,
    action = TextStyle(
        fontFamily = karmaticArcade(),
        fontSize = 16.sp,
        color = colors.action,
        textAlign = TextAlign.Center
    ),
    sectionTitle = TextStyle(
        fontFamily = karmaticArcade(),
        fontSize = 14.sp,
        color = colors.sectionTitle
    ),
    move = TextStyle(
        fontFamily = byteBounce(),
        fontSize = 20.sp,
        color = colors.move
    ),
    boardCoordinates = TextStyle(
        fontWeight = FontWeight.Bold,
        color = colors.boardCoordinates
    )
)
