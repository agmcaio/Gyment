import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gps.gyment.data.enums.Muscle
import org.koin.mp.KoinPlatform
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen2(
    navController: NavController,
    //viewModel: ExerciseViewModel = hiltViewModel()
) {
    val exerciseRepository: ExerciseRepository = KoinPlatform.getKoin().get()
    val viewModel: ExerciseViewModel = getViewModel()

    val context = LocalContext.current
    //val students by viewModel.students.observeAsState(emptyList())

    // Usando a função fetchStudents, por exemplo
    LaunchedEffect(Unit) {
        exerciseRepository.fetchStudents(
            onSuccess = { students ->
                // Processar a lista de alunos
            },
            onError = { exception ->
                // Tratar o erro
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Criar Exercício") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            // Seus campos de entrada e botão aqui...
            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = {  viewModel.name.value = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.sets.value,
                onValueChange = { viewModel.sets.value = it },
                label = { Text("Quantidade de séries") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.repetitions.value,
                onValueChange = { viewModel.repetitions.value = it },
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
                    value = viewModel.selectedMuscle.value.displayName,
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
                                viewModel.selectedMuscle.value = muscle
                                expanded = false
                            },
                            text = { Text(muscle.displayName) }
                        )
                    }
                }
            }
            // Outros campos...

            Button(
                onClick = {
                    // Aqui você pode usar o exerciseRepository para adicionar o exercício
                    //val exerciseData = hashMapOf(
                        //"name" to viewModel.name.value,
                        // Adicione outros campos do exercício aqui
                    //)

                    val exerciseData = hashMapOf<String, Any?>(
                        "name" to viewModel.name.value,
                        "sets" to viewModel.sets.value,
                        "repetitions" to viewModel.repetitions.value,
                        "muscle_group" to viewModel.selectedMuscle.value.displayName,
                        "done" to false,
                        "created_at" to SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(
                            Date()
                        ),
                        "done_at" to null
                    )

                    exerciseRepository.addExercise(
                        exerciseData,
                        onSuccess = {
                            Toast.makeText(context, "Exercício criado com sucesso", Toast.LENGTH_SHORT).show()
                            viewModel.name.value = ""
                            viewModel.sets.value = ""
                            viewModel.repetitions.value = ""
                        },
                        onError = { exception ->
                            Toast.makeText(context, "Falha ao criar exercício", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar")
            }
        }
    }
}
