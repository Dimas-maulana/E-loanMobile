package com.example.eloanmust.core.designsystem.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// E-LOAN MUST - THEME CONFIGURATION
// ============================================
// Premium Finance Theme with Gold/Black accent
// Supports both Light and Dark modes

private val ELoanLightColorScheme = lightColorScheme(
    // Primary - Gold
    primary = Gold70,
    onPrimary = Dark20,
    primaryContainer = Gold90,
    onPrimaryContainer = Gold10,

    // Secondary - Dark
    secondary = Dark40,
    onSecondary = Color.White,
    secondaryContainer = Dark90,
    onSecondaryContainer = Dark10,

    // Tertiary - Accent Gold
    tertiary = AccentAmber,
    onTertiary = Dark20,
    tertiaryContainer = Gold95,
    onTertiaryContainer = Gold20,

    // Error
    error = ErrorMain,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,

    // Background & Surface
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,

    // Other
    outline = BorderLight,
    outlineVariant = Dark90,
    scrim = Scrim,
    inverseSurface = Dark20,
    inverseOnSurface = Dark95,
    inversePrimary = Gold80,
    surfaceTint = Gold70,
)

private val ELoanDarkColorScheme = darkColorScheme(
    // Primary - Gold
    primary = Gold70,
    onPrimary = Dark20,
    primaryContainer = Gold30,
    onPrimaryContainer = Gold90,

    // Secondary - Dark
    secondary = Dark70,
    onSecondary = Dark20,
    secondaryContainer = Dark40,
    onSecondaryContainer = Dark90,

    // Tertiary - Accent Gold
    tertiary = AccentAmber,
    onTertiary = Dark20,
    tertiaryContainer = Gold30,
    onTertiaryContainer = Gold90,

    // Error
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,

    // Background & Surface
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    // Other
    outline = BorderDark,
    outlineVariant = Dark50,
    scrim = Scrim,
    inverseSurface = Dark90,
    inverseOnSurface = Dark20,
    inversePrimary = Gold40,
    surfaceTint = Gold70,
)

@Composable
fun EloanMustTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Disabled by default to maintain brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ELoanDarkColorScheme
        else -> ELoanLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color
            window.statusBarColor = if (darkTheme) {
                BackgroundDark.toArgb()
            } else {
                Gold70.toArgb()
            }
            // Set status bar icon color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ELoanTypography,
        shapes = ELoanShapes,
        content = content
    )
}

// ============================================
// THEME EXTENSIONS
// ============================================

// Extended color properties for semantic colors
object ELoanExtendedColors {
    val success = SuccessMain
    val successLight = SuccessLight
    val successDark = SuccessDark

    val warning = WarningMain
    val warningLight = WarningLight
    val warningDark = WarningDark

    val info = InfoMain
    val infoLight = InfoLight
    val infoDark = InfoDark

    val error = ErrorMain
    val errorLight = ErrorLight
    val errorDark = ErrorDark

    // Loan Status Colors
    val statusPending = StatusPending
    val statusReviewed = StatusReviewed
    val statusApproved = StatusApproved
    val statusRejected = StatusRejected
    val statusDisbursed = StatusDisbursed
}
