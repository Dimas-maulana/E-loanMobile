package com.example.eloanmust.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ============================================
// E-LOAN MUST - PREMIUM FINANCE COLOR PALETTE
// ============================================
// Theme: Modern Finance with Gold/Black accent
// Style: Premium, Professional, Trustworthy

// Primary Colors - Gold/Amber (Trust & Prosperity)
val Gold10 = Color(0xFF3D2E00)
val Gold20 = Color(0xFF635100)
val Gold30 = Color(0xFF8C7400)
val Gold40 = Color(0xFFB69900)
val Gold50 = Color(0xFFD4B200)
val Gold60 = Color(0xFFF5CE00)
val Gold70 = Color(0xFFFFD700) // Primary Gold
val Gold80 = Color(0xFFFFE066)
val Gold90 = Color(0xFFFFF0B3)
val Gold95 = Color(0xFFFFF8E1)
val Gold99 = Color(0xFFFFFDF5)

// Secondary Colors - Dark Navy/Charcoal (Stability & Trust)
val Dark10 = Color(0xFF0D0D0D)
val Dark20 = Color(0xFF1A1A1A)
val Dark30 = Color(0xFF262626)
val Dark40 = Color(0xFF333333)
val Dark50 = Color(0xFF404040)
val Dark60 = Color(0xFF595959)
val Dark70 = Color(0xFF737373)
val Dark80 = Color(0xFF999999)
val Dark90 = Color(0xFFCCCCCC)
val Dark95 = Color(0xFFE6E6E6)
val Dark99 = Color(0xFFF5F5F5)

// Accent Colors
val AccentGold = Color(0xFFF5A623)
val AccentAmber = Color(0xFFFFB300)
val AccentBronze = Color(0xFFCD7F32)

// Semantic Colors - Status
val Success10 = Color(0xFF002200)
val Success20 = Color(0xFF004D00)
val Success30 = Color(0xFF006600)
val Success40 = Color(0xFF008000)
val Success50 = Color(0xFF00B300)
val Success60 = Color(0xFF00CC00)
val Success70 = Color(0xFF33FF33)
val Success80 = Color(0xFF66FF66)
val Success90 = Color(0xFFB3FFB3)
val SuccessMain = Color(0xFF43A047)
val SuccessLight = Color(0xFFE8F5E9)
val SuccessDark = Color(0xFF2E7D32)

val Error10 = Color(0xFF410002)
val Error20 = Color(0xFF690005)
val Error30 = Color(0xFF93000A)
val Error40 = Color(0xFFBA1A1A)
val Error50 = Color(0xFFDE3730)
val Error60 = Color(0xFFFF5449)
val Error70 = Color(0xFFFF897D)
val Error80 = Color(0xFFFFB4AB)
val Error90 = Color(0xFFFFDAD6)
val ErrorMain = Color(0xFFE53935)
val ErrorLight = Color(0xFFFFEBEE)
val ErrorDark = Color(0xFFC62828)

val Warning10 = Color(0xFF3D2E00)
val Warning20 = Color(0xFF5C4500)
val Warning30 = Color(0xFF7A5C00)
val Warning40 = Color(0xFF996E00)
val Warning50 = Color(0xFFB88000)
val Warning60 = Color(0xFFD69400)
val Warning70 = Color(0xFFF5A800)
val Warning80 = Color(0xFFFFCC4D)
val Warning90 = Color(0xFFFFE699)
val WarningMain = Color(0xFFFFA726)
val WarningLight = Color(0xFFFFF3E0)
val WarningDark = Color(0xFFF57C00)

val Info10 = Color(0xFF001D36)
val Info20 = Color(0xFF003258)
val Info30 = Color(0xFF00497E)
val Info40 = Color(0xFF0061A4)
val Info50 = Color(0xFF007AC9)
val Info60 = Color(0xFF0092F0)
val Info70 = Color(0xFF5CB4FF)
val Info80 = Color(0xFF9ECDFF)
val Info90 = Color(0xFFD1E4FF)
val InfoMain = Color(0xFF1976D2)
val InfoLight = Color(0xFFE3F2FD)
val InfoDark = Color(0xFF1565C0)

// Background Colors
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)
val SurfaceVariantLight = Color(0xFFF5F5F5)
val SurfaceVariantDark = Color(0xFF2D2D2D)

// Text Colors
val OnPrimaryLight = Color(0xFF1A1A1A)
val OnPrimaryDark = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF1A1A1A)
val OnBackgroundDark = Color(0xFFE6E6E6)
val OnSurfaceLight = Color(0xFF1A1A1A)
val OnSurfaceDark = Color(0xFFE6E6E6)
val TextSecondaryLight = Color(0xFF666666)
val TextSecondaryDark = Color(0xFFAAAAAA)
val TextDisabledLight = Color(0xFF9E9E9E)
val TextDisabledDark = Color(0xFF666666)

// Gradient Colors
val GradientGoldStart = Color(0xFFFFD700)
val GradientGoldEnd = Color(0xFFF5A623)
val GradientDarkStart = Color(0xFF1A1A1A)
val GradientDarkEnd = Color(0xFF333333)

// Overlay Colors
val Scrim = Color(0x99000000)
val Overlay = Color(0x80000000)

// Border Colors
val BorderLight = Color(0xFFE0E0E0)
val BorderDark = Color(0xFF424242)

// Loan Status Colors
val StatusPending = Color(0xFFFF9800)
val StatusReviewed = Color(0xFF2196F3)
val StatusApproved = Color(0xFF4CAF50)
val StatusRejected = Color(0xFFF44336)
val StatusDisbursed = Color(0xFF9C27B0)

// Color utility object for accessing color collections
object ELoanColors {
    val gradientGold = listOf(GradientGoldStart, GradientGoldEnd)
    val gradientDark = listOf(GradientDarkStart, GradientDarkEnd)
}

/**
 * Get text color for loan status
 */
fun getLoanStatusColor(status: String): Color {
    return when (status) {
        "PENDING_REVIEW" -> StatusPending
        "REVIEWED" -> StatusReviewed
        "APPROVED" -> StatusApproved
        "REJECTED" -> StatusRejected
        "DISBURSED" -> StatusDisbursed
        else -> StatusPending
    }
}

/**
 * Get background color for loan status badge
 */
fun getLoanStatusBackgroundColor(status: String): Color {
    return when (status) {
        "PENDING_REVIEW" -> StatusPending.copy(alpha = 0.15f)
        "REVIEWED" -> StatusReviewed.copy(alpha = 0.15f)
        "APPROVED" -> StatusApproved.copy(alpha = 0.15f)
        "REJECTED" -> StatusRejected.copy(alpha = 0.15f)
        "DISBURSED" -> StatusDisbursed.copy(alpha = 0.15f)
        else -> StatusPending.copy(alpha = 0.15f)
    }
}

