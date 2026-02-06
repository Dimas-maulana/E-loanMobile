package com.example.eloanmust.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.designsystem.theme.Gold70
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.eloanmust.core.common.createTempImageFile
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Navigate -> {
                    if (event.route == "login") onNavigateToLogin()
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                actions = {
                    if (state.isLoggedIn && !state.isEditing) {
                        IconButton(onClick = { viewModel.onEvent(ProfileEvent.ToggleEdit) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        }
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
            if (!state.isLoggedIn) {
            // Not logged in - show login required
            LoginRequiredContent(
                message = "Silakan login untuk melihat profil Anda",
                onLoginClick = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues)
            )
        } else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold70)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Header
                ProfileHeader(username = state.username, email = state.email, isComplete = state.isProfileComplete)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profile Incomplete Warning
                AnimatedVisibility(visible = !state.isProfileComplete) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFB8860B))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Profil Belum Lengkap", fontWeight = FontWeight.SemiBold, color = Color(0xFF856404))
                                Text("Lengkapi profil untuk mengajukan pinjaman", style = MaterialTheme.typography.bodySmall, color = Color(0xFF856404))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Profile Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Data Pribadi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ProfileTextField(
                            value = state.fullName,
                            onValueChange = { viewModel.onEvent(ProfileEvent.FullNameChanged(it)) },
                            label = "Nama Lengkap",
                            enabled = state.isEditing
                        )
                        
                        ProfileTextField(
                            value = state.nik,
                            onValueChange = { viewModel.onEvent(ProfileEvent.NikChanged(it)) },
                            label = "NIK (16 digit)",
                            enabled = state.isEditing,
                            keyboardType = KeyboardType.Number
                        )
                        

                        
                        var showDatePicker by remember { mutableStateOf(false) }
                        if (showDatePicker) {
                            val datePickerState = rememberDatePickerState()
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                                            viewModel.onEvent(ProfileEvent.BirthDateChanged(formatter.format(Date(millis))))
                                        }
                                        showDatePicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Batal")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        Box(modifier = Modifier.clickable(enabled = state.isEditing) { showDatePicker = true }) {
                            ProfileTextField(
                                value = state.birthDate,
                                onValueChange = {},
                                label = "Tanggal Lahir (YYYY-MM-DD)",
                                enabled = false 
                            )
                            // Transparent overlay to verify click capture if needed, 
                            // but modifiers on Box generally work best for "read only" look inputs
                            Box(modifier = Modifier.matchParentSize().clickable(enabled = state.isEditing) { showDatePicker = true })
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // KTP Upload Section
                        Text("Foto KTP", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val context = LocalContext.current
                        var tempImageFile by remember { mutableStateOf<File?>(null) }
                        
                        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                            if (success && tempImageFile != null) {
                                viewModel.uploadKtp(tempImageFile!!)
                            }
                        }
                        
                        // Camera permission launcher
                        val cameraPermissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if (isGranted) {
                                // Permission granted, launch camera
                                tempImageFile = createTempImageFile(context)
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    tempImageFile!!
                                )
                                cameraLauncher.launch(uri)
                            }
                        }
                        
                        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                            uri?.let { 
                                val inputStream = context.contentResolver.openInputStream(it)
                                val file = createTempImageFile(context)
                                inputStream?.use { input ->
                                    file.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                viewModel.uploadKtp(file)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (state.ktpImageUrl != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(state.ktpImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "KTP Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                                        Text("Belum ada foto KTP", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        
                        if (state.isEditing) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { 
                                        // Request camera permission first
                                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Kamera")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Galeri")
                                }
                            }
                        }
                        

                        
                        ProfileTextField(
                            value = state.address,
                            onValueChange = { viewModel.onEvent(ProfileEvent.AddressChanged(it)) },
                            label = "Alamat",
                            enabled = state.isEditing,
                            minLines = 2
                        )
                        

                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bank Account Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rekening Bank", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        

                        ProfileTextField(
                            value = state.bankName,
                            onValueChange = { viewModel.onEvent(ProfileEvent.BankNameChanged(it)) },
                            label = "Nama Bank",
                            enabled = state.isEditing
                        )

                        ProfileTextField(
                            value = state.bankAccountNumber,
                            onValueChange = { viewModel.onEvent(ProfileEvent.BankAccountNumberChanged(it)) },
                            label = "Nomor Rekening",
                            enabled = state.isEditing,
                            keyboardType = KeyboardType.Number
                        )

                        ProfileTextField(
                            value = state.bankAccountName,
                            onValueChange = { viewModel.onEvent(ProfileEvent.BankAccountNameChanged(it)) },
                            label = "Nama Pemilik Rekening",
                            enabled = state.isEditing
                        )
                    }
                }
                
                // Action Buttons
                AnimatedVisibility(visible = state.isEditing) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { viewModel.onEvent(ProfileEvent.ToggleEdit) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Batal")
                            }
                            
                            Spacer(Modifier.width(12.dp))
                            
                            Button(
                                onClick = { viewModel.onEvent(ProfileEvent.Save) },
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Gold70, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Simpan")
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Logout Button
                OutlinedButton(
                    onClick = { viewModel.onEvent(ProfileEvent.Logout) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Keluar")
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
}

@Composable
private fun ProfileHeader(username: String, email: String, isComplete: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Gold70),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.Black, modifier = Modifier.size(32.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(email, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isComplete) Color(0xFF4CAF50) else Color(0xFFFFC107))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isComplete) "Profil Lengkap" else "Profil Belum Lengkap",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isComplete) Color(0xFF4CAF50) else Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodySmall, 
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = minLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White.copy(alpha = 0.7f),
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                disabledBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                disabledContainerColor = Color.White.copy(alpha = 0.05f)
            )
        )
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
                imageVector = Icons.Default.Person,
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
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

