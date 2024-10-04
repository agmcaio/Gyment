import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gps.gyment.Routes
import com.gps.gyment.ui.components.Logo
import com.gps.gyment.ui.screens.GoToPersonalArea
import com.gps.gyment.ui.screens.GoToRegisterButton
import com.gps.gyment.ui.screens.LoginForm

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate(Routes.HOME.route)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Logo()
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoginForm(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    onLoginClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            loginViewModel.login(email, password)
                        }
                    }
                )

                when (loginState) {
                    is LoginState.Loading -> CircularProgressIndicator()
                    is LoginState.Error -> {
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    else -> Unit
                }
            }
            GoToPersonalArea { navController.navigate("personalLogin") }
            GoToRegisterButton { navController.navigate("register") }
        }
    }
}
