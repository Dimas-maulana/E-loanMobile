package com.example.eloanmust.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
) {
    object Home : BottomNavItem(
        route = "home_tab",
        title = "Beranda",
        icon = Icons.Default.Home
    )
    
    object Notification : BottomNavItem(
        route = "notification_tab",
        title = "Notifikasi",
        icon = Icons.Default.Notifications
    )
}
