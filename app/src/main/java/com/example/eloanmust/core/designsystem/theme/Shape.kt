package com.example.eloanmust.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================
// E-LOAN MUST - SHAPE SYSTEM
// ============================================
// Consistent rounded corners throughout the app
// Premium finance aesthetic with smooth edges

val ELoanShapes = Shapes(
    // Extra Small - Chips, small badges
    extraSmall = RoundedCornerShape(4.dp),

    // Small - Buttons, text fields
    small = RoundedCornerShape(8.dp),

    // Medium - Cards, dialogs
    medium = RoundedCornerShape(12.dp),

    // Large - Bottom sheets, large cards
    large = RoundedCornerShape(16.dp),

    // Extra Large - Modal sheets
    extraLarge = RoundedCornerShape(24.dp),
)

// Custom Shape Definitions
object ELoanCornerRadius {
    val None = 0.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val Full = 50.dp // For circular/pill shapes
}

// Card Shapes
object ELoanCardShapes {
    val default = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)
    val small = RoundedCornerShape(8.dp)
    val topRounded = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val bottomRounded = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
}

// Button Shapes
object ELoanButtonShapes {
    val default = RoundedCornerShape(12.dp)
    val pill = RoundedCornerShape(50)
    val small = RoundedCornerShape(8.dp)
}

// Input Field Shapes
object ELoanInputShapes {
    val default = RoundedCornerShape(12.dp)
    val small = RoundedCornerShape(8.dp)
}

// Bottom Sheet Shapes
object ELoanBottomSheetShapes {
    val default = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val small = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}
