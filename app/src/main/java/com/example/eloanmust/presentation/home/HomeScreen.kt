package com.example.eloanmust.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eloanmust.core.designsystem.theme.Gold70
import com.example.eloanmust.feature.notification.presentation.NotificationScreen
import com.example.eloanmust.feature.notification.presentation.NotificationViewModel

@Composable
fun HomeScreen(
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    var currentRoute by rememberSaveable { mutableStateOf(BottomNavItem.Home.route) }
    val notificationState by notificationViewModel.state.collectAsState()
    
    // Local data class for navigation items
    data class NavItem(
        val route: String,
        val title: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val badgeCount: Int = 0
    )

    // Bottom Navigation Items
    // We update the badge count dynamically based on the ViewModel state
    val items = listOf(
        NavItem(
            route = BottomNavItem.Home.route,
            title = BottomNavItem.Home.title,
            icon = BottomNavItem.Home.icon
        ),
        NavItem(
            route = BottomNavItem.Notification.route,
            title = BottomNavItem.Notification.title,
            icon = BottomNavItem.Notification.icon,
            badgeCount = notificationState.unreadCount
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentRoute = item.route },
                        label = { Text(text = item.title) },
                        icon = {
                            if (item.badgeCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge { Text(text = item.badgeCount.toString()) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Gold70,
                            selectedTextColor = Gold70,
                            indicatorColor = Gold70.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF2C3E50)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                BottomNavItem.Home.route -> {
                    // Existing Home Content
                    HomeContent()
                }
                BottomNavItem.Notification.route -> {
                    // Notification Logic is handled inside NotificationScreen
                    // We need to pass the viewModel we share or let hilt inject it (but sharing allows badge update)
                    // Since we inject it at HomeScreen level to get unreadCount for badge, we should pass it down
                    // or just let it be re-injected (Hilt viewModels are scoped to NavGraph entry, or Activity/Fragment)
                    // Here we are inside one Composable, so passing the instance ensures same state.
                    NotificationScreen(
                        onNavigateToLoanDetail = { /* TODO: Navigate to Detail */ },
                        viewModel = notificationViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                                append("E-Loan ")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFFF1C40F), fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                                append("Must")
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF34495E)
                )
            )
        },
        containerColor = Color(0xFF2C3E50)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(text = "Welcome to Home Screen", color = Color.White)
            // Add more home content here
        }
    }
}
