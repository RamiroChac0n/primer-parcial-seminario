package com.example.primerparcial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.primerparcial.ui.navigation.AppNavigation
import com.example.primerparcial.ui.theme.PrimerParcialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimerParcialTheme {
                AppNavigation()
            }
        }
    }
}