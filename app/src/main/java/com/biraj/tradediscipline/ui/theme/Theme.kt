package com.biraj.tradediscipline.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF1F2937),
    secondary = Color(0xFF2563EB),
    tertiary = Color(0xFF16A34A),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFDC2626)
)

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFE5E7EB),
    secondary = Color(0xFF93C5FD),
    tertiary = Color(0xFF86EFAC),
    background = Color(0xFF0F172A),
    surface = Color(0xFF111827),
    error = Color(0xFFF87171)
)

@Composable
fun TradingDisciplineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
