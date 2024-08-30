package com.gps.gyment

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gps.gyment.ui.components.BottomNavBar
import com.gps.gyment.ui.screens.CreateExerciseScreen
import com.gps.gyment.ui.screens.ExerciseDetailScreen
import com.gps.gyment.ui.screens.HistoryScreen
import com.gps.gyment.ui.screens.HomeScreen
import com.gps.gyment.ui.screens.LoginScreen
import com.gps.gyment.ui.screens.ProfileScreen
import com.gps.gyment.ui.screens.RegisterScreen


@Composable
fun GymentApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, items = Routes.entries) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Routes.HOME.route) { HomeScreen(navController) }
            composable(route = Routes.HISTORY.route) { HistoryScreen(navController) }
            composable(route = Routes.PROFILE.route) { ProfileScreen(navController) }
            composable(route = "create_exercise") { CreateExerciseScreen(navController) }

            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("exercise_detail/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                ExerciseDetailScreen(exerciseId = exerciseId ?: "", navController = navController)
            }
        }
    }
}