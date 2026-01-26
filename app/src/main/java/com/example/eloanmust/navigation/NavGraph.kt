package com.example.eloanmust.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eloanmust.core.designsystem.theme.Gold70
import com.example.eloanmust.feature.auth.presentation.forgot_password.ForgotPasswordScreen
import com.example.eloanmust.feature.auth.presentation.login.LoginScreen
import com.example.eloanmust.feature.auth.presentation.register.RegisterScreen
import com.example.eloanmust.feature.home.presentation.HomeScreen

/**
 * Main Navigation Graph for the application.
 * Starts from Home (guest can browse products)
 */
@Composable
fun EloanNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                EloanBottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            // ============================================
            // MAIN SCREENS (with bottom nav)
            // ============================================
            
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToSimulation = {
                        navController.navigate(Screen.LoanSimulation.route)
                    },
                    onNavigateToApplyLoan = {
                        navController.navigate(Screen.LoanApply.route)
                    },
                    onNavigateToHistory = {
                        navController.navigate(Screen.LoanHistory.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
            
            composable(Screen.LoanHistory.route) {
                PlaceholderScreen(
                    title = "Riwayat Pinjaman",
                    showBackButton = false
                )
            }
            
            composable(Screen.Notifications.route) {
                PlaceholderScreen(
                    title = "Notifikasi",
                    showBackButton = false
                )
            }
            
            composable(Screen.Profile.route) {
                PlaceholderScreen(
                    title = "Profil",
                    showBackButton = false
                )
            }
            
            // ============================================
            // AUTH SCREENS
            // ============================================
            
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // ============================================
            // LOAN SCREENS
            // ============================================
            
            composable(Screen.LoanSimulation.route) {
                PlaceholderScreen(
                    title = "Simulasi Pinjaman",
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.LoanApply.route) {
                PlaceholderScreen(
                    title = "Ajukan Pinjaman",
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.LoanDetail.route,
                arguments = listOf(navArgument("loanId") { type = NavType.LongType })
            ) { backStackEntry ->
                val loanId = backStackEntry.arguments?.getLong("loanId") ?: 0L
                PlaceholderScreen(
                    title = "Detail Pinjaman #$loanId",
                    onBack = { navController.popBackStack() }
                )
            }
            
            // ============================================
            // PROFILE SCREENS
            // ============================================
            
            composable(Screen.EditProfile.route) {
                PlaceholderScreen(
                    title = "Edit Profil",
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.UploadKtp.route) {
                PlaceholderScreen(
                    title = "Upload KTP",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun EloanBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Gold70,
                    selectedTextColor = Gold70,
                    indicatorColor = Gold70.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    showBackButton: Boolean = true,
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (showBackButton) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onBack) {
                    Text("Kembali", color = Gold70)
                }
            }
        }
    }
}
