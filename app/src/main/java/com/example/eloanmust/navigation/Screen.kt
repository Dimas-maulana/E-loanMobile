package com.example.eloanmust.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Landing : Screen("landing")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object ResetPassword : Screen("reset_password/{token}") {
        fun createRoute(token: String) = "reset_password/$token"
    }

    // Main
    data object Home : Screen("home")
    data object Products : Screen("products")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")

    // Loan
    data object LoanSimulation : Screen("loan_simulation")
    data object LoanApply : Screen("loan_apply")
    data object LoanHistory : Screen("loan_history")
    data object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: Long) = "loan_detail/$loanId"
    }

    // Profile
    data object EditProfile : Screen("edit_profile")
    data object UploadKtp : Screen("upload_ktp")

    // Settings
    data object Settings : Screen("settings")
    data object About : Screen("about")
}

/**
 * Bottom navigation items
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * List of bottom navigation items
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Beranda",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.LoanHistory.route,
        label = "Pinjaman",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    ),
    BottomNavItem(
        route = Screen.Notifications.route,
        label = "Notifikasi",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        label = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

/**
 * Routes that should show bottom navigation bar
 */
val bottomNavRoutes = bottomNavItems.map { it.route }

/**
 * Routes that don't require authentication
 */
val publicRoutes = listOf(
    Screen.Splash.route,
    Screen.Landing.route,
    Screen.Login.route,
    Screen.Register.route,
    Screen.ForgotPassword.route,
    Screen.ResetPassword.route,
    Screen.LoanSimulation.route // Public simulation
)
