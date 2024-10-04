package com.gps.gyment

import CreateExerciseScreen2
import EditExerciseScreen
import HistoryScreen2
import HomeScreen
import LoginScreen
import ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gps.gyment.ui.components.BottomNavBar
import com.gps.gyment.ui.screens.ExerciseDetailScreen2
import com.gps.gyment.ui.screens.LoginScreenPersonal
import com.gps.gyment.ui.screens.RegisterScreen


@Composable
fun GymentApp(startRoute: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "register" && currentRoute != "personalLogin") {
                BottomNavBar(navController = navController, items = Routes.entries)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME.route) { HomeScreen(navController) }
            composable(Routes.HISTORY.route) { HistoryScreen2(navController) }
            composable(Routes.PROFILE.route) { ProfileScreen(navController) }
            composable("create_exercise") { CreateExerciseScreen2 (navController) }
            composable("login") { LoginScreen(navController) }
            composable("personalLogin") { LoginScreenPersonal(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("exercise_detail/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                ExerciseDetailScreen2(exerciseId = exerciseId ?: "", navController = navController)
            }

            composable("edit_exercise/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                EditExerciseScreen(navController = navController, exerciseId = exerciseId ?: "")
            }
        }
    }
}