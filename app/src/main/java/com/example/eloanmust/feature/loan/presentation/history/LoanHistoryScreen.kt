package com.example.eloanmust.feature.loan.presentation.history

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.common.toRupiah
import com.example.eloanmust.core.designsystem.theme.Gold70
import com.example.eloanmust.core.designsystem.theme.getLoanStatusBackgroundColor
import com.example.eloanmust.core.designsystem.theme.getLoanStatusColor
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanHistoryScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToApply: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: LoanHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pinjaman") },
                actions = {
                    if (state.isLoggedIn) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).padding(end = 16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { viewModel.refresh() }) {
                                Icon(Icons.Default.Refresh, "Refresh", tint = Color.Black)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (state.isLoggedIn) {
                FloatingActionButton(
                    onClick = onNavigateToApply,
                    containerColor = Gold70,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, "Apply Loan")
                }
            }
        }
    ) { paddingValues ->
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                )
        ) {
            if (!state.isLoggedIn) {
            // Not logged in - show login required
            LoginRequiredContent(
                message = "Silakan login untuk melihat riwayat pinjaman Anda",
                onLoginClick = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(paddingValues)
            ) {
                if (state.isLoading && state.loans.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Gold70)
                    }
                } else if (state.error != null && state.loans.isEmpty()) {
                    ErrorContent(
                        message = state.error ?: "Gagal memuat data",
                        onRetry = { viewModel.loadLoans() }
                    )
                } else if (state.loans.isEmpty()) {
                    EmptyContent(onApply = onNavigateToApply)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        
                        items(state.loans) { loan ->
                            LoanCard(
                                loan = loan,
                                onClick = { onNavigateToDetail(loan.id) }
                            )
                        }
                        
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun LoanCard(
    loan: LoanDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pinjaman #${loan.id}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    loan.createdAt?.let {
                        Text(
                            text = formatDate(it),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getLoanStatusBackgroundColor(loan.status))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = getStatusDisplayName(loan.status),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = getLoanStatusColor(loan.status)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jumlah", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    Text(loan.amount.toRupiah(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Gold70)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tenor", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    Text("${loan.getEffectiveTenor()} bulan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Cicilan/bln", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    Text(loan.monthlyInstallment.toRupiah(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Gold70)
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(onApply: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum Ada Pinjaman",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Anda belum memiliki riwayat pinjaman. Ajukan pinjaman pertama Anda sekarang!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.Button(
                onClick = onApply,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Gold70,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Ajukan Pinjaman", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRetry) { Text("Coba Lagi", color = Gold70) }
        }
    }
}

@Composable
private fun LoginRequiredContent(
    message: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Login Diperlukan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.Button(
                onClick = onLoginClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Gold70,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun getStatusDisplayName(status: String): String {
    return when (status) {
        "PENDING_REVIEW" -> "Menunggu Review"
        "REVIEWED" -> "Sedang Ditinjau"
        "APPROVED" -> "Disetujui"
        "REJECTED" -> "Ditolak"
        "DISBURSED" -> "Dana Cair"
        else -> status
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

