package com.gps.gyment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.gps.gyment.ui.theme.GymentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            GymentTheme {
                val startRoute = if (FirebaseAuth.getInstance().currentUser != null) "app" else "login"
                GymentApp(startRoute = startRoute)
            }
        }
    }
}