package com.example.schooldiary.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.schooldiary.domain.AppTheme

fun colorSchemeFor(theme: AppTheme): ColorScheme = when (theme) {
    AppTheme.LIGHT -> LightColorScheme
    AppTheme.AMOLED -> AmoledColorScheme
    AppTheme.BLUE -> BlueColorScheme
    AppTheme.CYBER -> CyberColorScheme
}

fun extraColorsFor(theme: AppTheme): ExtraColors = when (theme) {
    AppTheme.LIGHT -> LightExtraColors
    AppTheme.AMOLED -> AmoledExtraColors
    AppTheme.BLUE -> BlueExtraColors
    AppTheme.CYBER -> CyberExtraColors
}

val LocalExtraColors = staticCompositionLocalOf { LightExtraColors }

val extraColors: ExtraColors
    @Composable @ReadOnlyComposable get() = LocalExtraColors.current

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun SchoolDiaryTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val colorScheme = colorSchemeFor(theme)
    val extra = extraColorsFor(theme)
    val isDark = theme != AppTheme.LIGHT

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, shapes = AppShapes) {
        CompositionLocalProvider(LocalExtraColors provides extra) {
            content()
        }
    }
}
