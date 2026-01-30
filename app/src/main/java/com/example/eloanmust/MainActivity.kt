package com.example.eloanmust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.eloanmust.core.designsystem.theme.EloanMustTheme
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
        enableEdgeToEdge()
        
        // Request notification permission on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        
        setContent {
            EloanMustTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EloanMustApp()
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
fun EloanMustApp() {
    val navController = rememberNavController()
    
    // Always start from Home - guest can browse products
    EloanNavGraph(
        navController = navController,
        startDestination = Screen.Home.route
    )
}
