package com.example.eloanmust.feature.loan.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun LoanDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoanDetailViewModel = hiltViewModel()
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
                title = { Text("Detail Pinjaman") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
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
            if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold70)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { viewModel.refresh() }) { Text("Coba Lagi", color = Gold70) }
                }
            }
        } else {
            state.loan?.let { loan ->
                LoanDetailContent(
                    loan = loan,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
}

@Composable
private fun LoanDetailContent(
    loan: LoanDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(getLoanStatusBackgroundColor(loan.status)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getStatusIcon(loan.status),
                        contentDescription = null,
                        tint = getLoanStatusColor(loan.status),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = getStatusTitle(loan.status),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gold70
                )
                
                Text(
                    text = getStatusDescription(loan.status),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = loan.amount.toRupiah(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detail Pinjaman", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                DetailRow(Icons.Default.Receipt, "ID Pinjaman", "#${loan.id}")
                DetailRow(Icons.Default.AttachMoney, "Jumlah Pinjaman", loan.amount.toRupiah())
                DetailRow(Icons.Default.CalendarMonth, "Tenor", "${loan.getEffectiveTenor()} bulan")
                DetailRow(Icons.Default.AttachMoney, "Bunga", "${loan.getEffectiveInterestRate()}%")
                DetailRow(Icons.Default.AttachMoney, "Total Bunga", loan.totalInterest.toRupiah())
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                
                DetailRow(Icons.Default.AttachMoney, "Total Pembayaran", loan.totalPayment.toRupiah(), isHighlight = true)
                DetailRow(Icons.Default.AttachMoney, "Cicilan/Bulan", loan.monthlyInstallment.toRupiah(), isHighlight = true)
                
                loan.createdAt?.let {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                    DetailRow(Icons.Default.AccessTime, "Tanggal Pengajuan", formatDate(it))
                }
            }
        }
        
        // Status Timeline
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Status Pengajuan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                
                StatusTimelineItem("Pengajuan Diterima", isCompleted = true, isActive = loan.status in listOf("PENDING_REVIEW", "SUBMITTED"))
                StatusTimelineItem("Sedang Direview", isCompleted = loan.status in listOf("REVIEWED", "APPROVED", "REJECTED", "DISBURSED"), isActive = loan.status in listOf("IN_REVIEW", "REVIEWED"))
                StatusTimelineItem("Keputusan", isCompleted = loan.status in listOf("APPROVED", "REJECTED", "DISBURSED"), isActive = loan.status in listOf("APPROVED", "REJECTED"))
                StatusTimelineItem("Dana Cair", isCompleted = loan.status == "DISBURSED", isActive = loan.status == "DISBURSED", isLast = true)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Gold70, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) Gold70 else Color.White
        )
    }
}

@Composable
private fun StatusTimelineItem(
    title: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> Gold70
                            isCompleted -> Gold70.copy(alpha = 0.5f)
                            else -> Color.White.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted || isActive) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(
                            if (isCompleted) Gold70.copy(alpha = 0.5f)
                            else Color.White.copy(alpha = 0.2f)
                        )
                )
            }
        }
        
        Spacer(Modifier.width(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) Gold70 else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

private fun getStatusIcon(status: String): ImageVector {
    return when (status) {
        "APPROVED", "DISBURSED" -> Icons.Default.CheckCircle
        "REJECTED" -> Icons.Default.Pending
        else -> Icons.Default.Pending
    }
}

private fun getStatusTitle(status: String): String {
    return when (status) {
        "PENDING_REVIEW" -> "Menunggu Review"
        "REVIEWED" -> "Sedang Ditinjau"
        "APPROVED" -> "Disetujui"
        "REJECTED" -> "Ditolak"
        "DISBURSED" -> "Dana Sudah Cair"
        else -> status
    }
}

private fun getStatusDescription(status: String): String {
    return when (status) {
        "PENDING_REVIEW" -> "Pengajuan Anda sedang dalam antrian review"
        "REVIEWED" -> "Pengajuan sedang ditinjau oleh tim kami"
        "APPROVED" -> "Selamat! Pengajuan Anda disetujui"
        "REJECTED" -> "Maaf, pengajuan Anda tidak memenuhi kriteria"
        "DISBURSED" -> "Dana telah ditransfer ke rekening Anda"
        else -> ""
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) { dateString }
}
