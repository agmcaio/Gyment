package com.gps.gyment.ui.screens

import ExerciseRepository
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gps.gyment.R
import com.gps.gyment.data.enums.getMuscleByName
import com.gps.gyment.ui.viewmodels.ExerciseDetailViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.mp.KoinPlatform
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen2(
    exerciseId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val exerciseRepository: ExerciseRepository = KoinPlatform.getKoin().get()
    val viewModel: ExerciseDetailViewModel = getViewModel()
    //val viewModel: ExerciseDetailViewModel = viewModel(factory = ExerciseDetailViewModelFactory(repository))
    viewModel.fetchExercise(exerciseId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = viewModel.exercise?.value?.name ?: "Detalhes do Exercício", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Text(
                        text = getMuscleByName(viewModel.exercise?.value?.muscleGroup ?: "")?.displayName ?: "",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.workout),
                    contentDescription = "Imagem do Exercício",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Séries: ${viewModel.exercise?.value?.sets ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Repetições: ${viewModel.exercise?.value?.repetitions ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.markAsDone(
                            exerciseId,
                            onSuccess = {
                                Toast.makeText(context, "Exercício feito!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { e ->
                                Toast.makeText(context, e.message ?: "Erro ao marcar exercício como feito.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = viewModel.exercise != null && !viewModel.isDone.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (viewModel.isDone.value) {
                            "Realizado"
                        } else "Marcar como realizado",
                    )
                }

                Button(
                    onClick = {
                        viewModel.deleteExercise(
                            exerciseId,
                            onSuccess = {
                                Toast.makeText(context, "Exercício deletado!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { e ->
                                Toast.makeText(context, e.message ?: "Erro ao deletar exercício.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = viewModel.exercise != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Deletar Exercício")
                }

                // Botão para editar o exercício
                Button(
                    onClick = {
                        navController.navigate("edit_exercise/$exerciseId") // Navegação para a tela de edição
                    },
                    enabled = viewModel.exercise != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Cor do botão de editar
                ) {
                    Text(text = "Editar Exercício", color = MaterialTheme.colorScheme.onPrimary) // Texto em branco para contraste
                }
            }
        }
    )
}
