package com.example.parkingappfront_end.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


//  Tema Terracotta e Crema
private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val ParkingAppColorScheme = lightColorScheme(
    primary = Color(0xFF007BFF), // Blu principale
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFF90CAF9),
    surfaceDim = Color(0xFF6750A4),
    surfaceBright = Color(0xFFD0BCFF),
    surfaceContainerLowest = Color(0xFFEFFBFF),
    surfaceContainerLow = Color(0xFFD0E4FF),
    surfaceContainer = Color(0xFFB3CDFF),
    surfaceContainerHigh = Color(0xFF97B3FF),
    surfaceContainerHighest = Color(0xFF7C9AFF)
)

private val ParkingAppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF007BFF), // Blu principale (mantenuto anche in dark mode)
    onPrimary = Color.Black, // Testo su primario in dark mode
    primaryContainer = Color(0xFF0056A3), //Contenitore primario più scuro
    onPrimaryContainer = Color(0xFFD0E4FF), // Testo su contenitore primario in dark mode
    secondary = Color(0xFFCCC2DC), // Secondario più chiaro per dark mode
    onSecondary = Color.Black, // Testo su secondario in dark mode
    secondaryContainer = Color(0xFF4A4458), // Contenitore secondario più scuro
    onSecondaryContainer = Color(0xFFE8DEF8), // Testo su contenitore secondario in dark mode
    tertiary = Color(0xFFEFB8C8), // Terziario più chiaro per dark mode
    onTertiary = Color.Black, // Testo su terziario in dark mode
    tertiaryContainer = Color(0xFF633B48), // Contenitore terziario più scuro
    onTertiaryContainer = Color(0xFFFFD8E4), // Testo su contenitore terziario in dark mode
    error = Color(0xFFF2B8B5), // Errore più chiaro per dark mode
    onError = Color.Black, // Testo su errore in dark mode
    errorContainer = Color(0xFF8C1D18), // Contenitore errore più scuro
    onErrorContainer = Color(0xFFF9DEDC), // Testo su contenitore errore in dark mode
    background = Color(0xFF1C1B1F), // Sfondo scuro
    onBackground = Color.White, // Testo su sfondo in dark mode
    surface = Color(0xFF1C1B1F), // Superficie scura
    onSurface = Color.White, // Testo su superficie in dark mode
    surfaceVariant = Color(0xFF49454F), // Variantesuperficie più chiara per dark mode
    onSurfaceVariant = Color(0xFFCAC4D0), // Testo su variante superficie in dark mode
    outline = Color(0xFF938F99), // Outline più chiaro per dark mode
    outlineVariant = Color(0xFF49454F), // Variante outline più scura per dark mode
    scrim = Color(0xFF000000), // Scrim nero
    inverseSurface = Color(0xFFE6E1E5), // Superficie inversa più scura per dark mode
    inverseOnSurface = Color(0xFF313033), // Testo su superficie inversa in dark mode
    inversePrimary = Color(0xFF007BFF), // Primario inverso (blu)
    surfaceDim = Color(0xFF6750A4), // Superficie attenuata
    surfaceBright = Color(0xFFD0BCFF), // Superficie luminosa
    surfaceContainerLowest = Color(0xFF000000), // Contenitore superficie più scuro
    surfaceContainerLow = Color(0xFF1C1B1F), // Contenitore superficie scuro
    surfaceContainer = Color(0xFF333136), // Contenitore superficie medio
    surfaceContainerHigh = Color(0xFF49454F), // Contenitore superficie chiaro
    surfaceContainerHighest = Color(0xFF635B68) // Contenitore superficie più chiaro
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)


@Composable
fun ParkingAppFrontEndTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
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