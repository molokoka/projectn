package molokoka.project.n.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalAppColors = staticCompositionLocalOf { AppColors() }

private val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("No AppTypography provided - wrap the content in AppTheme")
}

private val LocalAppDimens = staticCompositionLocalOf { AppDimens() }

object AppTheme {

    val colors: AppColors
        @Composable get() = LocalAppColors.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current

    val dimens: AppDimens
        @Composable get() = LocalAppDimens.current
}

@Composable
fun AppTheme(
    colors: AppColors = AppColors(),
    typography: AppTypography = defaultTypography(colors),
    dimens: AppDimens = AppDimens(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppDimens provides dimens,
        LocalIndication provides AppIndication(colors.pressedOverlay),
        content = content
    )
}
