package com.receipts.receipt_sharing.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    surface = Gray10,
    onSurface = Color.White,
    primary = GreenYellow30,
    onPrimary = Color.White,
    primaryContainer = GreenYellow10,
    onPrimaryContainer = GreenYellow90,
    secondary = BlueGreen50,
    onSecondary = Color.White,
    secondaryContainer = BlueGreen10,
    onSecondaryContainer = BlueGreen90,
    error = Red40,
    onError = Color.White,
    tertiary = Blue30,
    onTertiary = Color.White,
    tertiaryContainer = Blue20,
    onTertiaryContainer = Blue90
)

private val LightColorScheme = lightColorScheme(
    surface = Color.White,
    onSurface = Color.Black,
    primary = GreenYellow50,
    onPrimary = Color.Black,
    primaryContainer = GreenYellow80,
    onPrimaryContainer = GreenYellow20,
    secondary = BlueGreen50,
    onSecondary = Color.Black,
    secondaryContainer = BlueGreen90,
    onSecondaryContainer = BlueGreen20,
    error = Red50,
    onError = Gray90,
    tertiary = Blue50,
    onTertiary = Color.Black,
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue20
)

@Composable
fun RecipeSharing_theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}