package com.gps.gyment.ui.screens

import RegisterViewModel
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gps.gyment.Routes
import com.gps.gyment.ui.components.Logo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
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

    LaunchedEffect(registerViewModel.cep) {
        if (registerViewModel.cep.length == 8) { // Verifica se o CEP possui 8 dígitos
            fetchAddressByCep(registerViewModel.cep,onSuccess = { address ->
                registerViewModel.rua = address.street ?:  ""
                registerViewModel.bairro = address.neighborhood ?:  ""
                registerViewModel.cidade= address.city ?:  ""
            })
        }
    }

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
                cep = registerViewModel.cep,
                city = registerViewModel.cidade,
                street = registerViewModel.rua,
                neighborhood = registerViewModel.bairro,
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
                onStreetChange = {registerViewModel.rua = it},
                onCityChange = {registerViewModel.cidade=it},
                onCepChange = {registerViewModel.cep = it},
                onNeighborhood = {registerViewModel.bairro = it},
                isLoading = isLoading
            )
            BackToLoginButton { navController.popBackStack() }
        }
    }
}


private suspend fun fetchAddressByCep(cep: String, onSuccess: (Address) -> Unit) {
    // Mova a chamada de rede para a thread IO
    withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://viacep.com.br/ws/$cep/json/")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody)

                val street = jsonObject.getString("logradouro")
                val neighborhood = jsonObject.getString("bairro")
                val city = jsonObject.getString("localidade")

                // Retorne os dados na thread principal
                onSuccess(Address(street, neighborhood, city))
            } else {
                Log.e("FetchAddress", "Erro ao buscar endereço: ${response.message}")
            }
        }
    }
}

data class Address(val street: String?, val neighborhood: String?, val city: String?)