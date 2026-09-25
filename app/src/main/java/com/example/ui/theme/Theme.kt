package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Retro City Pop Color Schemes
private val CityPopDarkColorScheme = darkColorScheme(
    primary = CrimsonRed,               // #CE3232
    onPrimary = VintageCream,           // #F0EAD6
    primaryContainer = CrimsonRedDark,
    onPrimaryContainer = VintageCreamLight,
    secondary = WaveTeal,               // #4A7C8C
    onSecondary = VintageCream,
    secondaryContainer = WaveTealDark,
    onSecondaryContainer = VintageCreamLight,
    tertiary = SunsetGold,
    onTertiary = DeepNavy,
    background = DeepNavy,              // #151C2B
    onBackground = VintageCream,        // #F0EAD6
    surface = DeepNavyDark,
    onSurface = VintageCream,
    surfaceVariant = DeepNavyCard,
    onSurfaceVariant = VintageCreamMuted,
    outline = WaveTealDark,
    outlineVariant = WaveTeal
)

private val CityPopLightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    onPrimary = VintageCream,
    primaryContainer = CrimsonRedLight,
    onPrimaryContainer = DeepNavy,
    secondary = WaveTeal,
    onSecondary = VintageCream,
    secondaryContainer = WaveTealLight,
    onSecondaryContainer = DeepNavy,
    tertiary = SunsetGold,
    onTertiary = DeepNavy,
    background = VintageCream,
    onBackground = DeepNavyText,
    surface = VintageCreamDarker,
    onSurface = DeepNavyText,
    surfaceVariant = VintageCream,
    onSurfaceVariant = DeepNavyMuted,
    outline = WaveTeal,
    outlineVariant = WaveTealLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Retro City Pop authentic Deep Navy aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CityPopDarkColorScheme else CityPopLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
