package com.landmarkgroup.sahlawarehouse.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary = SahlaPrimary,
    onPrimary = SahlaOnPrimary,
    primaryContainer = SahlaPrimaryVariant,
    secondary = SahlaSecondary,
    background = SahlaBackground,
    onBackground = SahlaOnBackground,
    surface = SahlaSurface,
    onSurface = SahlaOnBackground,
    error = SahlaError
)

private val DarkColors = darkColorScheme(
    primary = SahlaPrimary,
    onPrimary = SahlaOnPrimary,
    secondary = SahlaSecondary,
    error = SahlaError
)

@Composable
fun SahlaWarehouseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SahlaTypography,
        content = content
    )
}
