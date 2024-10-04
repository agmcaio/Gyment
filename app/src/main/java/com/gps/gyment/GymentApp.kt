package com.gps.gyment

import CreateExerciseScreen2
import HistoryScreen2
import HomeScreen2
import LoginScreen2
import ProfileScreen2
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
import com.gps.gyment.ui.screens.CreateExerciseScreen
import com.gps.gyment.ui.screens.ExerciseDetailScreen
import com.gps.gyment.ui.screens.ExerciseDetailScreen2
import com.gps.gyment.ui.screens.HistoryScreen
import com.gps.gyment.ui.screens.HomeScreen
import com.gps.gyment.ui.screens.LoginScreen
import com.gps.gyment.ui.screens.LoginScreenPersonal
import com.gps.gyment.ui.screens.ProfileScreen
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
            //composable(Routes.HOME.route) { HomeScreen(navController) }
            composable(Routes.HOME.route) { HomeScreen2(navController) }
            //composable(Routes.HISTORY.route) { HistoryScreen(navController) }
            composable(Routes.HISTORY.route) { HistoryScreen2(navController) }
            //composable(Routes.PROFILE.route) { ProfileScreen(navController) }
            composable(Routes.PROFILE.route) { ProfileScreen2(navController) }
            //composable("create_exercise") { CreateExerciseScreen(navController) }
            composable("create_exercise") { CreateExerciseScreen2 (navController) }
            //composable("login") { LoginScreen(navController) }
            composable("login") { LoginScreen2(navController) }
            composable("personalLogin") { LoginScreenPersonal(navController) }
            composable("register") { RegisterScreen(navController) }
            //composable("exercise_detail/{exerciseId}") { backStackEntry ->
                //val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                //ExerciseDetailScreen(exerciseId = exerciseId ?: "", navController = navController)
            //}
            composable("exercise_detail/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                ExerciseDetailScreen2(exerciseId = exerciseId ?: "", navController = navController)
            }
        }
    }
}