package com.gps.gyment.ui.screens

import RegisterViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gps.gyment.Routes
import com.gps.gyment.ui.components.Logo
import org.koin.androidx.compose.getViewModel

@Composable
fun RegisterScreen2(navController: NavController) {
    val registerViewModel: RegisterViewModel = getViewModel()

    val isLoading by registerViewModel::isLoading
    val nameError by remember { registerViewModel::nameError }
    var emailError by remember { registerViewModel::emailError }
    val passwordError by remember { registerViewModel::passwordError }
    val confirmPasswordError by remember { registerViewModel::confirmPasswordError }

    // Resto do código permanece inalterado

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Logo()
            Form(
                name = registerViewModel.name,
                onNameChange = { registerViewModel.name = it },
                nameError = nameError,
                email = registerViewModel.email,
                onEmailChange = { registerViewModel.email = it },
                emailError = emailError,
                password = registerViewModel.password,
                onPasswordChange = { registerViewModel.password = it },
                passwordError = passwordError,
                confirmPassword = registerViewModel.confirmPassword,
                onConfirmPasswordChange = { registerViewModel.confirmPassword = it },
                confirmPasswordError = confirmPasswordError,
                userType = registerViewModel.userType,
                onUserTypeChange = { registerViewModel.userType = it },
                onRegisterClick = {
                    registerViewModel.registerUser(
                        onComplete = {
                            navController.navigate(Routes.HOME.route)
                        },
                        onError = { errorMessage ->
                            emailError = errorMessage
                        }
                    )
                },
                isLoading = isLoading
            )
            BackToLoginButton { navController.popBackStack() }
        }
    }
}
