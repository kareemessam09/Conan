package com.example.conan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = PureWhite,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = SoftWhite,
    secondary = SecondaryGreen,
    onSecondary = CharcoalGray,
    secondaryContainer = TertiaryGreen,
    onSecondaryContainer = DarkGray,
    tertiary = TertiaryGreen,
    onTertiary = CharcoalGray,
    background = CharcoalGray,
    onBackground = SoftWhite,
    surface = SurfaceDark,
    onSurface = SoftWhite,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGray,
    error = SoftRed,
    onError = PureWhite,
    errorContainer = SoftRed,
    onErrorContainer = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = PureWhite,
    primaryContainer = TertiaryGreen,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = SecondaryGreen,
    onSecondary = PureWhite,
    secondaryContainer = TertiaryGreen,
    onSecondaryContainer = DarkGray,
    tertiary = TertiaryGreen,
    onTertiary = DarkGray,
    background = BackgroundLight,
    onBackground = CharcoalGray,
    surface = SurfaceLight,
    onSurface = CharcoalGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    error = SoftRed,
    onError = PureWhite,
    errorContainer = SoftRed,
    onErrorContainer = PureWhite
)

@Composable
fun PurityPathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}