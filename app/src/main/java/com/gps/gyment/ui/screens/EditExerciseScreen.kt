import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.ui.viewmodels.ExerciseDetailViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.mp.KoinPlatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    navController: NavController,
    exerciseId: String
) {
    val exerciseRepository: ExerciseRepository = KoinPlatform.getKoin().get()
    val viewModel: ExerciseDetailViewModel = getViewModel()
    val context = LocalContext.current

    LaunchedEffect(exerciseId) {
        viewModel.fetchExercise(exerciseId) // Chama a função fetchExercise
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Editar Exercício") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Verifica se o exercício foi carregado
            viewModel.exercise.value?.let { exercise ->
                OutlinedTextField(
                    value = exercise.name,
                    onValueChange = { viewModel.exercise.value = exercise.copy(name = it) },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exercise.sets.toString(),
                    onValueChange = { viewModel.exercise.value = exercise.copy(sets = it.toString()) },
                    label = { Text("Quantidade de séries") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exercise.repetitions.toString(),
                    onValueChange = { viewModel.exercise.value = exercise.copy(repetitions = it.toString()) },
                    label = { Text("Quantidade de repetições") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.exercise.value?.muscleGroup ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Muscle.entries.forEach { muscle ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.exercise.value = exercise.copy(muscleGroup = muscle.displayName)
                                    expanded = false
                                },
                                text = { Text(muscle.displayName) }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        // Atualiza o exercício no repositório
                        val exerciseData = hashMapOf<String, Any?>(
                            "name" to viewModel.exercise.value?.name,
                            "sets" to viewModel.exercise.value?.sets,
                            "repetitions" to viewModel.exercise.value?.repetitions,
                            "muscle_group" to viewModel.exercise.value?.muscleGroup
                        )

                        viewModel.updateExercise(
                            exerciseId,
                            exerciseData,
                            onSuccess = {
                                Toast.makeText(context, "Exercício atualizado com sucesso", Toast.LENGTH_SHORT).show()
                                navController.navigateUp() // Volta para a tela anterior após a atualização
                            },
                            onError = { exception ->
                                Toast.makeText(context, "Falha ao atualizar exercício", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Atualizar")
                }
            } ?: run {
                CircularProgressIndicator()
            }
        }
    }
}