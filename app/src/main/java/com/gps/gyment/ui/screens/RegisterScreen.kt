package com.gps.gyment.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.gps.gyment.Routes
import com.gps.gyment.ui.components.Logo
import com.gps.gyment.ui.theme.GymentTheme
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("aluno") }
    var cep by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Logo()
            Form(
                name = name,
                onNameChange = { name = it },
                nameError = nameError,
                email = email,
                cep = cep,
                city = city,
                street = street,
                neighborhood = neighborhood,
                onEmailChange = { email = it },
                emailError = emailError,
                password = password,
                onPasswordChange = { password = it },
                passwordError = passwordError,
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                confirmPasswordError = confirmPasswordError,
                userType = userType,
                onUserTypeChange = { userType = it },
                onRegisterClick = {
                    nameError = if (name.isEmpty()) "Nome não pode ser vazio" else null
                    emailError = if (email.isEmpty()) "E-mail não pode ser vazio" else null
                    passwordError = if (password.isEmpty()) "Senha não pode ser vazia" else null
                    confirmPasswordError = if (confirmPassword.isEmpty()) "Confirme sua senha" else null

                    if (password != confirmPassword) {
                        confirmPasswordError = "As senhas não correspondem"
                    }

                    if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
                        isLoading = true
                        registerUser(name, email, password, userType, navController, { isLoading = false }, { errorMessage ->
                            emailError = errorMessage
                            isLoading = false
                        })
                    }
                },
                onStreetChange = {street = it},
                onCityChange = {city =it},
                onCepChange = {cep = it},
                onNeighborhood = {neighborhood = it},
                isLoading = isLoading
            )
            BackToLoginButton{ navController.popBackStack() }
        }
    }
}

private fun registerUser(
    name: String,
    email: String,
    password: String,
    userType: String,
    navController: NavController,
    onComplete: () -> Unit,
    onError: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    try {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete()
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "userType" to userType,
                            "createdAt" to System.currentTimeMillis()
                        )

                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onComplete()
                                navController.navigate(Routes.HOME.route)
                            }
                            .addOnFailureListener { e ->
                                onError("Erro ao criar usuário: ${task.exception?.message}")
                            }
                    }
                } else {
                    onError("Erro ao criar usuário: ${task.exception?.message}")
                }
            }
    } catch (e: Exception) {
        onError("Erro ao criar usuário: ${e.message}")
    }

}


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