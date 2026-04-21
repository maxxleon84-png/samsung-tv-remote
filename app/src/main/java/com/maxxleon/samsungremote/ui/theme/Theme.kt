package com.maxxleon.samsungremote.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme = darkColorScheme(
    background = BrandBg,
    surface = BrandSurface,
    primary = BrandPrimary,
    secondary = BrandAccent,
    onBackground = BrandText,
    onSurface = BrandText,
    onPrimary = BrandText
)

@Composable
fun SamsungRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = AppTypography,
        content = content
    )
}
