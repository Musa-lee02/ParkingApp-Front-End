package com.example.parkingappfront_end.ui.theme

import android.R.id.primary
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


private val ModernLightBluePrimary = Color(0xFF2962FF) // Blu chiaro principale
private val ModernLightBlueSecondary = Color(0xFF64B5F6) // Blu chiaro secondario
private val ModernGray = Color(0xFFE0E0E0) // Grigio chiaro per sfondi
private val ModernDarkGray = Color(0xFF616161) // Grigio scuro per testo secondario
private val ModernWhite = Color(0xFFFFFFFF) // Bianco
private val ModernBlack = Color(0xFF000000) // Nero
private val ModernAccent = Color(0xFF00BCD4) // Colore accent per elementi interattivi
private val ModernError = Color(0xFFB00020) // Colore per errori



private val ParkingBluePrimary = Color(0xFF1565C0) // Blu principale
private val ParkingBlueSecondary = Color(0xFF1E88E5) // Blu secondario
private val ParkingGray = Color(0xFFECEFF1) // Grigio chiaro per sfondi
private val ParkingDarkGray = Color(0xFF37474F) // Grigio scuro per testo secondario e dettagli
private val ParkingWhite = Color(0xFFFFFFFF) // Bianco
private val ParkingBlack = Color(0xFF000000) // Nero
private val ParkingAccent = Color(0xFF00E676) // Verde per elementi interattivi e conferme
private val ParkingError = Color(0xFFD32F2F) // Rosso per errori






private val DarkBlueColorScheme = darkColorScheme(
    primary = ParkingBluePrimary, // Blu principale
    onPrimary = ParkingWhite, // Testo bianco su blu principale
    primaryContainer = ParkingBlueSecondary, // Contenitore blu secondario
    onPrimaryContainer = ParkingWhite, // Testo bianco su contenitore blu secondario
    secondary = ParkingBlueSecondary, // Blu secondario
    onSecondary = ParkingWhite, // Testo bianco su blu secondario
    secondaryContainer = ParkingDarkGray, // Contenitore grigio scuro
    onSecondaryContainer = ParkingWhite, // Testo bianco su contenitore grigio scuro
    tertiary = ParkingDarkGray, // Grigio scuro per testo secondario
    onTertiary = ParkingWhite, // Testo bianco su grigio scuro
    tertiaryContainer = ParkingGray, // Contenitore grigio chiaro
    onTertiaryContainer = ParkingBlack, // Testo nero su contenitore grigio chiaro
    background = ParkingBlack, // Sfondo nero
    onBackground = ParkingWhite, // Testo bianco su sfondo nero
    surface = ParkingDarkGray, // Superficie grigio scuro
    onSurface = ParkingWhite, // Testo bianco su superficie grigio scuro
    surfaceVariant = ParkingGray, // Superficie variante grigio chiaro
    onSurfaceVariant = ParkingDarkGray, // Testo grigio scuro su superficie variante
    outline = ParkingGray, // Bordo grigio chiaro
    inverseSurface = ParkingGray, // Superficie inversa grigio chiaro
    inverseOnSurface = ParkingBlack, // Testo nero su superficie inversa
    inversePrimary = ParkingAccent, // Verde per elementi interattivi
    error = ParkingError, // Rosso per errori
    onError = ParkingWhite, // Testo bianco su colore per errori
    errorContainer = ParkingError.copy(alpha = 0.2f), // Contenitore errori
    onErrorContainer = ParkingWhite, // Testo bianco su contenitore errori
)


private val LightBlueColorScheme = lightColorScheme(
    primary = ParkingBluePrimary, // Blu principale
    onPrimary = ParkingWhite, // Testo bianco su blu principale
    primaryContainer = ParkingBlueSecondary, // Contenitore blu secondario
    onPrimaryContainer = ParkingBlack, // Testo nero su contenitore blu secondario
    secondary = ParkingBlueSecondary, // Blu secondario
    onSecondary = ParkingWhite, // Testo bianco su blu secondario
    secondaryContainer = ParkingGray, // Contenitore grigio chiaro
    onSecondaryContainer = ParkingBlack, // Testo nero su contenitore grigio chiaro
    tertiary = ParkingGray, // Grigio chiaro per testo secondario
    onTertiary = ParkingBlack, // Testo nero su grigio chiaro
    tertiaryContainer = ParkingDarkGray, // Contenitore grigio scuro
    onTertiaryContainer = ParkingWhite, // Testo bianco su contenitore grigio scuro
    background = ParkingWhite, // Sfondo bianco
    onBackground = ParkingBlack, // Testo nero su sfondo bianco
    surface = ParkingWhite, // Superficie bianca
    onSurface = ParkingBlack, // Testo nero su superficie bianca
    surfaceVariant = ParkingGray, // Superficie variante grigio chiaro
    onSurfaceVariant = ParkingDarkGray, // Testo grigio scuro su superficie variante
    outline = ParkingDarkGray, // Bordo grigio scuro
    inverseSurface = ParkingDarkGray, // Superficie inversa grigio scuro
    inverseOnSurface = ParkingWhite, // Testo bianco su superficie inversa
    inversePrimary = ParkingAccent, // Verde per elementi interattivi
    error = ParkingError, // Rosso per errori
    onError = ParkingWhite, // Testo bianco su colore per errori
    errorContainer = ParkingError.copy(alpha = 0.2f), // Contenitore errori
    onErrorContainer = ParkingWhite, // Testo bianco su contenitore errori
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

        darkTheme -> DarkBlueColorScheme
        else -> LightBlueColorScheme
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