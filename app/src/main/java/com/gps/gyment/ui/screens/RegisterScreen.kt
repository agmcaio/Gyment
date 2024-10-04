package com.gps.gyment.ui.screens

import RegisterViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun RegisterScreen(navController: NavController) {
    val registerViewModel: RegisterViewModel = getViewModel()

    val isLoading by registerViewModel::isLoading
    val nameError by remember { registerViewModel::nameError }
    var emailError by remember { registerViewModel::emailError }
    val passwordError by remember { registerViewModel::passwordError }
    val confirmPasswordError by remember { registerViewModel::confirmPasswordError }

    LaunchedEffect(registerViewModel.cep) {
        if (registerViewModel.cep.length == 8) {
            fetchAddressByCep(registerViewModel.cep, onSuccess = { address ->
                registerViewModel.rua = address.street ?: ""
                registerViewModel.bairro = address.neighborhood ?: ""
                registerViewModel.cidade = address.city ?: ""
            })
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
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
                onStreetChange = { registerViewModel.rua = it },
                onCityChange = { registerViewModel.cidade = it },
                onCepChange = { registerViewModel.cep = it },
                onNeighborhood = { registerViewModel.bairro = it },
                isLoading = isLoading
            )
            BackToLoginButton { navController.popBackStack() }
        }
    }
}


private suspend fun fetchAddressByCep(cep: String, onSuccess: (Address) -> Unit) {
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

                onSuccess(Address(street, neighborhood, city))
            } else {
                Log.e("FetchAddress", "Erro ao buscar endereço: ${response.message}")
            }
        }
    }
}

data class Address(val street: String?, val neighborhood: String?, val city: String?)

@Composable
fun BackToLoginButton(onBackToLogin: () -> Unit) {
    Button(
        onClick = onBackToLogin,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Voltar para o login",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Form(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    email: String,
    cep:String,
    neighborhood : String,
    street : String,
    city:String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordError: String?,
    userType: String,
    onUserTypeChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onCepChange : (String)->Unit,
    onNeighborhood : (String) -> Unit,
    onCityChange : (String)->Unit,
    onStreetChange : (String)->Unit,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Crie sua conta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    onNameChange(it)
                    if (nameError != null) onNameChange(it)
                },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            if (nameError != null) {
                Text(
                    text = nameError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    onEmailChange(it)
                    if (emailError != null) onEmailChange(it)
                },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = cep,
                onValueChange = onCepChange,
                label = { Text("CEP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = neighborhood,
                onValueChange = onNeighborhood,
                label = { Text("Bairro") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = street,
                onValueChange = onStreetChange,
                label = { Text("Rua") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("Cidade") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            if (emailError != null) {
                Text(
                    text = emailError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    onPasswordChange(it)
                    if (passwordError != null) onPasswordChange(it)
                },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            if (passwordError != null) {
                Text(
                    text = passwordError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    onConfirmPasswordChange(it)
                    if (confirmPasswordError != null) onConfirmPasswordChange(it)
                },
                label = { Text("Confirme sua senha") },
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPasswordError != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onRegisterClick() }
                )
            )
            if (confirmPasswordError != null) {
                Text(
                    text = confirmPasswordError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Selecione o tipo de usuário")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RadioButton(
                selected = userType == "aluno",
                onClick = { onUserTypeChange("aluno") }
            )
            Text("Aluno")

            RadioButton(
                selected = userType == "personal",
                onClick = { onUserTypeChange("personal") }
            )
            Text("Personal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Criar e acessar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}