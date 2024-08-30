package com.gps.gyment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.gps.gyment.ui.screens.HistoryScreen
import com.gps.gyment.ui.screens.HomeScreen
import com.gps.gyment.ui.screens.LoginScreen
import com.gps.gyment.ui.screens.ProfileScreen
import com.gps.gyment.ui.screens.RegisterScreen
import com.gps.gyment.ui.theme.GymentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            GymentTheme {
                val startRoute = if (FirebaseAuth.getInstance().currentUser != null) "app" else "login"
                val navController = rememberNavController()


                NavHost(navController = navController, startDestination = startRoute) {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }

                    composable("app") { GymentApp() }
                }
            }
        }
    }
}