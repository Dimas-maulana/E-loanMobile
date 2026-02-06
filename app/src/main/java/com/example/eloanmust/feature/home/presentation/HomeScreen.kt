package com.example.eloanmust.feature.home.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.eloanmust.R
import com.example.eloanmust.core.designsystem.theme.Blue60
import com.example.eloanmust.core.common.toRupiah
import com.example.eloanmust.core.designsystem.theme.ELoanColors
import com.example.eloanmust.core.designsystem.theme.Gold70
import com.example.eloanmust.feature.product.data.dto.PlafondDto

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSimulation: () -> Unit,
    onNavigateToApplyLoan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0f172a),
                        Color(0xFF1e293b)
                    )
                )
            ),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            HomeHeader(
                username = state.username,
                isLoggedIn = state.isLoggedIn,
                unreadNotificationCount = state.unreadNotificationCount,
                onLoginClick = onNavigateToLogin,
                onNotificationClick = onNavigateToNotifications,
                onProfileClick = onNavigateToProfile
            )
        }
        
        // Hero Section
        item {
            HeroSection(
                isLoggedIn = state.isLoggedIn,
                onApplyClick = {
                    viewModel.onApplyLoan(
                        onLoginRequired = onNavigateToLogin,
                        onProceed = onNavigateToApplyLoan
                    )
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Quick Actions
        item {
            QuickActionsSection(
                isLoggedIn = state.isLoggedIn,
                onSimulationClick = onNavigateToSimulation,
                onApplyClick = {
                    viewModel.onApplyLoan(
                        onLoginRequired = onNavigateToLogin,
                        onProceed = onNavigateToApplyLoan
                    )
                },
                onHistoryClick = {
                    if (state.isLoggedIn) onNavigateToHistory() else onNavigateToLogin()
                },
                onLoginClick = onNavigateToLogin
            )
        }
        
        // Products Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Produk Pinjaman",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Gold70,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = Gold70)
                    }
                }
            }
        }
        
        // Products List
        if (state.error != null && state.products.isEmpty()) {
            item {
                ErrorCard(
                    message = state.error ?: "Gagal memuat produk",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (state.products.isEmpty() && !state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada produk tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(state.products) { product ->
                ProductCard(
                    product = product,
                    onApplyClick = {
                        viewModel.onApplyLoan(
                            onLoginRequired = onNavigateToLogin,
                            onProceed = onNavigateToApplyLoan
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    username: String,
    isLoggedIn: Boolean,
    unreadNotificationCount: Int,
    onLoginClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0f172a),
                        Color(0xFF1e293b)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isLoggedIn) "Selamat Datang," else "Halo,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = if (isLoggedIn) username else "Guest",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Gold70
                )
            }
            
            Row {
                if (isLoggedIn) {
                    // Notification Icon with Badge
                    Box {
                        IconButton(onClick = onNotificationClick) {
                            Icon(Icons.Default.Notifications, "Notifications", tint = Color.White)
                        }
                        // Unread count badge
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Gold70.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person, "Profile",
                                tint = Gold70,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold70,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Login, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Login", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    isLoggedIn: Boolean,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0f172a),
                        Color(0xFF1e293b)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Logo and Brand Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_wolf),
                    contentDescription = "E-Loan Must Logo",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "E-Loan Must",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold70
                    )
                    Text(
                        text = "Electronic Loan · Mobile User · Simple Transaction",
                        style = MaterialTheme.typography.labelSmall,
                        color = Blue60
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Hero Content with Glassmorphism Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text Content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pinjaman Cepat dengan E-Loan Must",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Platform pinjaman digital legal dan aman yang dioptimalkan untuk pengguna mobile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dapatkan persetujuan dalam hitungan menit!",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Gold70
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onApplyClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold70,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isLoggedIn) "Ajukan Sekarang" else "Login & Ajukan",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Mascot Image
                    Image(
                        painter = painterResource(id = R.drawable.eloan),
                        contentDescription = "E-Loan Must Mascot",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    isLoggedIn: Boolean,
    onSimulationClick: () -> Unit,
    onApplyClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = "Menu Cepat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionItem(Icons.Default.Calculate, "Simulasi", onSimulationClick)
            QuickActionItem(Icons.Default.CreditCard, "Ajukan", onApplyClick)
            QuickActionItem(Icons.Default.History, "Riwayat", onHistoryClick)
            if (!isLoggedIn) {
                QuickActionItem(Icons.Default.Login, "Login", onLoginClick)
            }
        }
    }
}

@Composable
private fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = Gold70, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun ProductCard(
    product: PlafondDto,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    product.description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Gold70.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${product.interestRate}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold70
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Plafond",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${product.minAmount.toRupiah()} - ${product.maxAmount.toRupiah()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tenor",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "s/d ${product.maxTenor} bulan",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold70,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ajukan Pinjaman", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onRetry) {
                Text("Coba Lagi", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
