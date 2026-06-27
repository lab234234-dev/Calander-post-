package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PosterViewModel
import com.example.ui.PosterViewModelFactory
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable Edge-to-Edge styling to ensure the app occupies full-screen real estate comfortably.
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                // Main Container Surface styled in Dark/Indigo Theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Initialize the ViewModel using our custom Factory
                    val posterViewModel: PosterViewModel = viewModel(
                        factory = PosterViewModelFactory(application)
                    )
                    
                    // Render the Main application container screen
                    MainScreen(viewModel = posterViewModel)
                }
            }
        }
    }
}
