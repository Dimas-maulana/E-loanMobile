package com.example.eloanmust.feature.loan.presentation.simulation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.common.toRupiah
import com.example.eloanmust.core.designsystem.theme.Gold70
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSimulationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToApply: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    viewModel: LoanSimulationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Navigate -> {
                    when (event.route) {
                        "login" -> onNavigateToLogin()
                        "profile" -> onNavigateToProfile()
                    }
                }
                else -> {}
            }
        }
    }
    
    // Location client for loan submission
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { 
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) 
    }
    
    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false)
        
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.onEvent(LoanSimulationEvent.ConfirmApplyLoan(location.latitude, location.longitude))
                    } else {
                        android.widget.Toast.makeText(context, "Gagal mendapatkan lokasi. Pastikan GPS aktif.", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: SecurityException) {
                android.widget.Toast.makeText(context, "Error getting location", android.widget.Toast.LENGTH_LONG).show()
            }
        } else {
            android.widget.Toast.makeText(context, "Izin lokasi diperlukan untuk mengajukan pinjaman", android.widget.Toast.LENGTH_LONG).show()
        }
    }
    
    // Confirmation Dialog
    if (state.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LoanSimulationEvent.DismissConfirmDialog) },
            icon = {
                Icon(Icons.Default.Info, null, tint = Gold70, modifier = Modifier.size(48.dp))
            },
            title = {
                Text("Konfirmasi Pengajuan", textAlign = TextAlign.Center)
            },
            text = {
                Column {
                    Text(
                        "Apakah Anda yakin ingin mengajukan pinjaman ini?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Show summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Jumlah Pinjaman", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                Text(state.amount.toLongOrNull()?.toRupiah() ?: state.amount, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tenor", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                Text("${state.tenor} bulan", color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cicilan/bulan", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                Text(
                                    state.simulationResult?.monthlyInstallment?.toRupiah() ?: "-",
                                    color = Gold70,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        // Request location permission and then submit
                        if (androidx.core.app.ActivityCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                            androidx.core.app.ActivityCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    viewModel.onEvent(LoanSimulationEvent.ConfirmApplyLoan(location.latitude, location.longitude))
                                } else {
                                    android.widget.Toast.makeText(context, "Gagal mendapatkan lokasi. Coba buka Google Maps dulu.", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold70, contentColor = Color.Black)
                ) {
                    Text("Ya, Ajukan")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(LoanSimulationEvent.DismissConfirmDialog) }) {
                    Text("Tidak", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }
    
    // Success Dialog with Loan Details
    if (state.isSuccess) {
        val loanResult = state.loanResult
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LoanSimulationEvent.DismissSuccess) },
            icon = {
                Icon(Icons.Default.CheckCircle, null, tint = Gold70, modifier = Modifier.size(48.dp))
            },
            title = {
                Text("Pengajuan Berhasil!", textAlign = TextAlign.Center)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Pengajuan pinjaman Anda telah diterima dan sedang diproses.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (loanResult != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Loan Details Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Detail Pinjaman",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Gold70
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Loan ID
                                LoanDetailRow("ID Pinjaman", "#${loanResult.id}")
                                
                                // Customer Name
                                loanResult.customerName?.let {
                                    LoanDetailRow("Nama", it)
                                }
                                
                                // Product Name
                                loanResult.plafondName?.let {
                                    LoanDetailRow("Produk", it)
                                }
                                
                                // Amount
                                LoanDetailRow("Jumlah Pinjaman", loanResult.amount.toRupiah())
                                
                                // Tenor
                                LoanDetailRow("Tenor", "${loanResult.getEffectiveTenor()} bulan")
                                
                                // Interest Rate
                                val interestRate = loanResult.actualInterestRate ?: loanResult.baseInterestRate ?: loanResult.interestRate
                                LoanDetailRow("Bunga", "${interestRate}%/tahun")
                                
                                // Total Interest
                                LoanDetailRow("Total Bunga", loanResult.totalInterest.toRupiah())
                                
                                // Total Payment
                                LoanDetailRow("Total Pembayaran", loanResult.totalPayment.toRupiah())
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Monthly Installment (highlighted)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Cicilan/bulan",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        loanResult.monthlyInstallment.toRupiah(),
                                        color = Gold70,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Status",
                                        color = Color.White.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            loanResult.status,
                                            color = Color(0xFF3B82F6),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(LoanSimulationEvent.DismissSuccess)
                        onNavigateToHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold70, contentColor = Color.Black)
                ) {
                    Text("Lihat Riwayat")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    viewModel.onEvent(LoanSimulationEvent.DismissSuccess)
                    onNavigateBack()
                }) {
                    Text("Tutup", color = Gold70)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Simulasi Pinjaman") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A), // Dark blue bg
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F172A) // Dark blue bg match screenshot
    ) { paddingValues ->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
            
            Text(
                text = "Hitung estimasi cicilan pinjaman Anda secara real-time",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
            )

            // Main Card Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)), // Glassmorphism
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    
                    // Banner Cicilan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Perkiraan cicilan kamu",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Always show the result if available (local or remote), otherwise 0
                            Text(
                                text = state.simulationResult?.monthlyInstallment?.toRupiah() ?: "Rp 0",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isSimulating) Color.White.copy(alpha = 0.7f) else Color.White
                            )
                            
                            Text(
                                "/bulan",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                state.selectedPlafond?.let { plafond ->
                                    val plafondName = plafond.name ?: "Pinjaman"
                                    Text(
                                        "Produk: $plafondName",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold70
                                    )
                                    Text(" • ", color = Color.White.copy(alpha = 0.3f))
                                }
                                Text(
                                    "Tenor: ${state.tenor} bulan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Amount Input
                    Text("Jumlah Pinjaman", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { viewModel.onEvent(LoanSimulationEvent.AmountChanged(it)) },
                        leadingIcon = { Text("Rp", color = Color.White.copy(alpha = 0.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6), // Blue accent
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0xFF334155),
                            unfocusedContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    // Slider
                    Slider(
                        value = state.amount.replace("[^0-9]".toRegex(), "").toFloatOrNull() ?: 0f,
                        onValueChange = { viewModel.onEvent(LoanSimulationEvent.AmountChanged(it.toLong().toString())) },
                        valueRange = 1000000f..100000000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3B82F6),
                            activeTrackColor = Color(0xFF3B82F6),
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rp 1.000.000", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                        Text("Rp 100.000.000", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    // Product Info
                    state.selectedPlafond?.let { plafond ->
                         val name = plafond.name ?: "..."
                         val rawMax = plafond.maxTenor
                         val effectiveMax = if (rawMax > 0) rawMax else 60
                         
                         if (name != "null" && name.isNotEmpty()) {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                 Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                 Spacer(modifier = Modifier.width(4.dp))
                                 Text(
                                     "Product $name is available for your loan amount. Maximum tenor: $effectiveMax months.",
                                     color = Color(0xFF4CAF50),
                                     style = MaterialTheme.typography.labelSmall
                                 )
                             }
                         }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Tenor Selection
                    Text("Tenor (Bulan)", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    val tenorOptions = listOf(1, 3, 6, 9, 12, 18, 24, 30, 36, 42, 48, 54, 60)
                    
                    // Logic: If API returns 0 (unlimited/custom), treated as 60. Otherwise use the limit.
                    val rawMaxTenor = state.selectedPlafond?.maxTenor ?: 60
                    val effectiveMaxTenor = if (rawMaxTenor > 0) rawMaxTenor else 60
                    
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tenorOptions) { tenor ->
                            val isSelected = state.tenor == tenor.toString()
                            val isEnabled = tenor <= effectiveMaxTenor
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) Gold70
                                        else if (isEnabled) Color.White.copy(alpha = 0.1f)
                                        else Color.White.copy(alpha = 0.05f)
                                    )
                                    .clickable(enabled = isEnabled) { 
                                        viewModel.onEvent(LoanSimulationEvent.TenorChanged(tenor.toString())) 
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tenor.toString(),
                                    color = if (isSelected) Color.Black 
                                            else if (isEnabled) Color.White.copy(alpha = 0.8f)
                                            else Color.White.copy(alpha = 0.2f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    if (effectiveMaxTenor > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Maksimal tenor untuk produk ini: $effectiveMaxTenor bulan",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Result Cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Interest Rate
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Bunga Per Tahun", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${state.simulationResult?.interestRate ?: state.selectedPlafond?.interestRate ?: 0}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        
                        // Total Interest
                        Card(
                            modifier = Modifier.weight(1.5f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Bunga", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.simulationResult?.totalInterest?.toRupiah() ?: "-",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Total Payment
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A).copy(alpha = 0.5f)), // Dark blue/greenish
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                    ) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text("Total Pembayaran", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                             Spacer(modifier = Modifier.height(4.dp))
                             Text(
                                 state.simulationResult?.totalPayment?.toRupiah() ?: "-",
                                 style = MaterialTheme.typography.headlineSmall,
                                 fontWeight = FontWeight.Bold,
                                 color = Color.White
                             )
                         }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Action Button
                    Button(
                        onClick = { viewModel.onEvent(LoanSimulationEvent.ApplyLoan) },
                        enabled = !state.isSubmitting && state.simulationResult != null,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold70, // Yellow/Gold
                            contentColor = Color.Black,
                            disabledContainerColor = Gold70.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                        } else {
                            Text("Ajukan Pinjaman Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Error Display
                    state.simulationError?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning, 
                                    contentDescription = null, 
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Simulasi Gagal: $error",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    if (state.simulationResult == null && !state.isSimulating) {
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(
                             "* Masukkan nominal dan pilih tenor untuk melihat simulasi",
                             style = MaterialTheme.typography.labelSmall,
                             color = Color.White.copy(alpha = 0.5f),
                              textAlign = TextAlign.Center,
                             modifier = Modifier.fillMaxWidth()
                         )
                    }
                }
            }
        }
    }
}
}


/**
 * Helper composable for displaying loan detail rows
 */
@Composable
private fun LoanDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            value,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}
