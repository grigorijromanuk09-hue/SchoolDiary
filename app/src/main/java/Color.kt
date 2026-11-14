package com.example.schooldiary.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Цвета оценок для каждой темы. */
@Immutable
data class ExtraColors(
    val grade5: Color,
    val grade4: Color,
    val grade3: Color,
    val grade2: Color
)

// ---------- 1. Светлая ----------
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2F5BEA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3EAFF),
    onPrimaryContainer = Color(0xFF0B2A8A),
    secondary = Color(0xFF5F6B85),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3EAFF),
    onSecondaryContainer = Color(0xFF0B2A8A),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111418),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111418),
    surfaceVariant = Color(0xFFF0F1F4),
    onSurfaceVariant = Color(0xFF5B6070),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F8FA),
    surfaceContainer = Color(0xFFF2F3F5),
    surfaceContainerHigh = Color(0xFFECEEF1),
    surfaceContainerHighest = Color(0xFFE3E5EA),
    outline = Color(0xFFC9CCD3),
    outlineVariant = Color(0xFFE3E5EA),
    error = Color(0xFFD93025)
)
val LightExtraColors = ExtraColors(
    grade5 = Color(0xFF1FA35A),
    grade4 = Color(0xFF5B9A1F),
    grade3 = Color(0xFFE69500),
    grade2 = Color(0xFFE0383E)
)

// ---------- 2. Чёрная AMOLED ----------
val AmoledColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF0A1B33),
    primaryContainer = Color(0xFF1B2A41),
    onPrimaryContainer = Color(0xFFD3E3FD),
    secondary = Color(0xFFB0B6C2),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1F2733),
    onSecondaryContainer = Color(0xFFD3E3FD),
    background = Color(0xFF000000),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF121212),
    onSurfaceVariant = Color(0xFF9AA0A6),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0B0B0B),
    surfaceContainer = Color(0xFF121212),
    surfaceContainerHigh = Color(0xFF1A1A1A),
    surfaceContainerHighest = Color(0xFF222222),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF262626),
    error = Color(0xFFFF6B6B)
)
val AmoledExtraColors = ExtraColors(
    grade5 = Color(0xFF4ADE80),
    grade4 = Color(0xFFA3E635),
    grade3 = Color(0xFFFBBF24),
    grade2 = Color(0xFFF87171)
)

// ---------- 3. Синяя (Deep Blue) ----------
val BlueColorScheme = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color(0xFF00263A),
    primaryContainer = Color(0xFF1C3F85),
    onPrimaryContainer = Color(0xFFD6ECFF),
    secondary = Color(0xFF90CAF9),
    onSecondary = Color(0xFF0A1A3F),
    secondaryContainer = Color(0xFF1C3F85),
    onSecondaryContainer = Color(0xFFD6ECFF),
    background = Color(0xFF0A1A3F),
    onBackground = Color(0xFFEAF2FF),
    surface = Color(0xFF0A1A3F),
    onSurface = Color(0xFFEAF2FF),
    surfaceVariant = Color(0xFF16306B),
    onSurfaceVariant = Color(0xFFA8BEE6),
    surfaceContainerLowest = Color(0xFF081535),
    surfaceContainerLow = Color(0xFF0E2250),
    surfaceContainer = Color(0xFF12285C),
    surfaceContainerHigh = Color(0xFF183168),
    surfaceContainerHighest = Color(0xFF1E3A78),
    outline = Color(0xFF3C5A9E),
    outlineVariant = Color(0xFF24407F),
    error = Color(0xFFFF8A80)
)
val BlueExtraColors = ExtraColors(
    grade5 = Color(0xFF4ADE80),
    grade4 = Color(0xFF7DD3FC),
    grade3 = Color(0xFFFBBF24),
    grade2 = Color(0xFFF87171)
)

// ---------- 4. Чёрно-оранжевая (Cyber) ----------
val CyberColorScheme = darkColorScheme(
    primary = Color(0xFFFF6D00),
    onPrimary = Color(0xFF0A0A0A),
    primaryContainer = Color(0xFF3A1D00),
    onPrimaryContainer = Color(0xFFFFB074),
    secondary = Color(0xFFFF9E40),
    onSecondary = Color(0xFF0A0A0A),
    secondaryContainer = Color(0xFF2E1800),
    onSecondaryContainer = Color(0xFFFF6D00),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFF2F2F2),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFA3A3A3),
    surfaceContainerLowest = Color(0xFF050505),
    surfaceContainerLow = Color(0xFF111111),
    surfaceContainer = Color(0xFF161616),
    surfaceContainerHigh = Color(0xFF1D1D1D),
    surfaceContainerHighest = Color(0xFF242424),
    outline = Color(0xFF444444),
    outlineVariant = Color(0xFF2A2A2A),
    error = Color(0xFFFF5252)
)
val CyberExtraColors = ExtraColors(
    grade5 = Color(0xFFFF6D00), // неоново-оранжевые «пятёрки»
    grade4 = Color(0xFF69F0AE),
    grade3 = Color(0xFFFFD740),
    grade2 = Color(0xFFFF5252)
)
