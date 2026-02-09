package com.example.eloanmust

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.eloanmust.core.designsystem.theme.EloanMustTheme
import com.example.eloanmust.core.security.RootDetectionHelper
import com.example.eloanmust.navigation.EloanNavGraph
import com.example.eloanmust.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for E-Loan Must application.
 * App starts from Home screen (guest can browse products)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security: Prevent screen capture and recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Check if device is rooted
        val isRooted = RootDetectionHelper.isDeviceRooted()

        setContent {
            EloanMustTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EloanMustApp(showRootWarning = isRooted)
                }
            }
        }
    }
}

/**
 * Main App composable
 * Always starts from Home - login is required only when applying for loan
 */
@Composable
fun EloanMustApp(showRootWarning: Boolean = false) {
    val navController = rememberNavController()
    var showDialog by remember { mutableStateOf(showRootWarning) }

    // Root detection warning dialog
    if (showDialog) {
        RootWarningDialog(
            onDismiss = { showDialog = false }
        )
    }

    // Always start from Home - guest can browse products
    EloanNavGraph(
        navController = navController,
        startDestination = Screen.Home.route
    )
}

/**
 * Warning dialog shown when device is detected as rooted
 */
@Composable
fun RootWarningDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Peringatan Keamanan")
        },
        text = {
            Text(
                text = "Perangkat Anda terdeteksi sudah di-root. " +
                    "Penggunaan aplikasi pada perangkat yang di-root dapat menimbulkan risiko keamanan. " +
                    "Kami menyarankan untuk menggunakan perangkat yang tidak di-root untuk keamanan " +
                    "data dan transaksi Anda."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Saya Mengerti")
            }
        }
    )
}
