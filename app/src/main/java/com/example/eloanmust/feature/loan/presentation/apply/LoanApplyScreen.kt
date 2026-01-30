package com.example.eloanmust.feature.loan.presentation.apply

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.common.toRupiah
import com.example.eloanmust.core.designsystem.theme.Gold70
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: LoanApplyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Navigate -> {
                    when (event.route) {
                        "history" -> onNavigateToHistory()
                    }
                }
                else -> {}
            }
        }
    }
    
    // Application Success Dialog
    if (state.isSuccess) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onEvent(LoanApplyEvent.DismissSuccessDialog) },
            icon = {
                 Icon(Icons.Default.CheckCircle, null, tint = Gold70, modifier = Modifier.size(48.dp))
            },
            title = {
                Text(text = "Pengajuan Berhasil", textAlign = TextAlign.Center)
            },
            text = {
                Text(
                    "Pengajuan Anda telah diterima dan sedang menunggu verifikasi.", 
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = { viewModel.onEvent(LoanApplyEvent.DismissSuccessDialog) },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold70, contentColor = Color.Black)
                ) {
                    Text("Lihat Riwayat")
                }
            },
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ajukan Pinjaman") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Gold70,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        if (state.isSuccess) {
            // Success Screen
            SuccessScreen(
                onViewHistory = onNavigateToHistory,
                onBackToHome = onNavigateBack
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Warning
                AnimatedVisibility(visible = !state.isProfileComplete) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFB8860B))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Profil Belum Lengkap", fontWeight = FontWeight.SemiBold, color = Color(0xFF856404))
                                Text("Lengkapi profil untuk mengajukan pinjaman", style = MaterialTheme.typography.bodySmall, color = Color(0xFF856404))
                            }
                            Button(
                                onClick = onNavigateToProfile,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8860B))
                            ) {
                                Text("Lengkapi")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Amount Input with Slider
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nominal Pinjaman",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Display current amount
                        Text(
                            text = if (state.amount.isNotEmpty()) {
                                state.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull()?.toRupiah() ?: "Rp 0"
                            } else {
                                "Rp 0"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold70
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Amount Slider
                        Slider(
                            value = state.amount.replace("[^0-9]".toRegex(), "").toFloatOrNull() ?: 1000000f,
                            onValueChange = { 
                                viewModel.onEvent(LoanApplyEvent.AmountChanged(it.toLong().toString()))
                            },
                            valueRange = 1000000f..100000000f,
                            steps = 98,
                            colors = SliderDefaults.colors(
                                thumbColor = Gold70,
                                activeTrackColor = Gold70,
                                inactiveTrackColor = Gold70.copy(alpha = 0.3f)
                            )
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rp 1 Juta",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Rp 100 Juta",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (state.amountError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.amountError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Auto-detected Product
                AnimatedVisibility(visible = state.selectedPlafond != null) {
                    state.selectedPlafond?.let { plafond ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Gold70.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, null, tint = Gold70)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Produk Terdeteksi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = plafond.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Bunga ${plafond.interestRate}% • Max ${plafond.maxTenor} bulan",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold70
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tenor Input
                OutlinedTextField(
                    value = state.tenor,
                    onValueChange = { viewModel.onEvent(LoanApplyEvent.TenorChanged(it)) },
                    label = { Text("Tenor (Bulan)") },
                    placeholder = { Text("Contoh: 12") },
                    isError = state.tenorError != null,
                    supportingText = state.tenorError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold70,
                        focusedLabelColor = Gold70,
                        cursorColor = Gold70
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Purpose Input
                OutlinedTextField(
                    value = state.purpose,
                    onValueChange = { viewModel.onEvent(LoanApplyEvent.PurposeChanged(it)) },
                    label = { Text("Tujuan Pinjaman") },
                    placeholder = { Text("Contoh: Modal usaha, biaya pendidikan, dll") },
                    isError = state.purposeError != null,
                    supportingText = state.purposeError?.let { { Text(it) } },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold70,
                        focusedLabelColor = Gold70,
                        cursorColor = Gold70
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit Button
                Button(
                    onClick = { viewModel.onEvent(LoanApplyEvent.Submit) },
                    enabled = !state.isSubmitting && state.isProfileComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold70,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ajukan Pinjaman", fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PlafondItem(
    plafond: PlafondDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Gold70 else MaterialTheme.colorScheme.outline
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(if (isSelected) Gold70.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(plafond.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "${plafond.minAmount.toRupiah()} - ${plafond.maxAmount.toRupiah()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).background(Gold70),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    onViewHistory: () -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Gold70,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Pengajuan Berhasil!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pengajuan pinjaman Anda sedang diproses. Kami akan mengirimkan notifikasi untuk status selanjutnya.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onViewHistory,
                colors = ButtonDefaults.buttonColors(containerColor = Gold70, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Riwayat Pinjaman", fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Gold70),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke Beranda")
            }
        }
    }
}
