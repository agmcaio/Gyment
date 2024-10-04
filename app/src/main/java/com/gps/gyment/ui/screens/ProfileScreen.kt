import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gps.gyment.R
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
) {
    val profileViewModel: ProfileViewModel = getViewModel()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Perfil") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            if (userProfile != null) {
                OutlinedTextField(
                    value = userProfile!!.name,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userProfile!!.email,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userProfile!!.cep,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("CEP") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userProfile!!.rua,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Rua") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userProfile!!.bairro,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Bairro") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userProfile!!.cidade,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Cidade") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    profileViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sair")
            }
        }
    }
}
