package com.gps.gyment.ui.screens

import ExerciseRepository
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    viewModel.fetchExercise(exerciseId)

    var shakeDetector by remember { mutableStateOf(false) }
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val shakeListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {

                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]
                val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())


                if (magnitude > 12) {
                    shakeDetector = true
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    LaunchedEffect(Unit) {
        accelerometer?.also { sensor ->
            sensorManager.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    if (shakeDetector) {
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
        shakeDetector = false
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = viewModel.exercise?.name ?: "Detalhes do Exercício", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Text(
                        text = getMuscleByName(viewModel.exercise?.muscleGroup ?: "")?.displayName ?: "",
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
                                text = "Séries: ${viewModel.exercise?.sets ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Repetições: ${viewModel.exercise?.repetitions ?: "N/A"}",
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
                    enabled = viewModel.exercise != null && !viewModel.isDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (viewModel.isDone) "Realizado" else "Marcar como realizado",
                    )
                }
            }
        }
    )
}
