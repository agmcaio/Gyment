package com.gps.gyment.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.R

import com.gps.gyment.data.enums.getMuscleByName
import com.gps.gyment.data.models.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    navController: NavController
) {
    Log.d("ExerciseDetailScreen", "Exercise ID: $exerciseId")
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var isDone by remember { mutableStateOf(false) }

    if (currentUser != null) {
        db.collection("users")
            .document(currentUser.uid)
            .collection("exercises")
            .document(exerciseId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    exercise = Exercise(
                        id = document.getString("id") ?: "0",
                        name = document.getString("name") ?: "",
                        repetitions = document.getString("repetitions") ?: "",
                        sets = document.getString("sets") ?: "",
                        muscleGroup = document.getString("muscle_group") ?: "",
                        createdAt = document.getLong("created_at") ?: 0L,
                        done = document.getBoolean("done") ?: false,
                        doneAt = document.getLong("done_at") ?: 0L
                    )
                    isDone = exercise?.done ?: false
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Function to mark exercise as done
    fun markAsDone() {
        currentUser?.let {
            db.collection("users")
                .document(it.uid)
                .collection("exercises")
                .document(exerciseId)
                .update(
                    mapOf(
                        "done" to true,
                        "done_at" to System.currentTimeMillis()
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(context, "Exercicio feito!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    isDone = true
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = exercise?.name ?: "Detalhes do Exercício", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Text(
                        text = getMuscleByName(exercise?.muscleGroup ?: "")?.displayName ?: "",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                },

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
                                text = "Séries: ${exercise?.sets ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Repetições: ${exercise?.repetitions ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Button(
                    onClick = { markAsDone() },
                    enabled = !isDone,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isDone) "Realizado" else "Marcar como realizado",

                    )
                }
            }
        }
    )
}
