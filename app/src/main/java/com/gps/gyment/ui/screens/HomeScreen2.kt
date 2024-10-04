import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gps.gyment.R
import com.gps.gyment.ui.components.ExerciseCard
import com.gps.gyment.ui.components.MuscleFilter
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2(navController: NavController) {
    val viewModel: HomeViewModel = getViewModel()
    val userName by viewModel.userName.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val filteredExercises by viewModel.filteredExercises.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_exercise") }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Exercício")
            }
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 32.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Olá,",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* Ação do botão de notificações */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Botão de notificações",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MuscleFilter(viewModel.selectedMuscle) { muscle ->
                viewModel.onMuscleSelected(muscle)
            }

            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Exercícios",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = "${filteredExercises.size}")
                }

                filteredExercises.forEach { exercise ->
                    ExerciseCard(exercise = exercise) {
                        navController.navigate("exercise_detail/${exercise.id}")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
