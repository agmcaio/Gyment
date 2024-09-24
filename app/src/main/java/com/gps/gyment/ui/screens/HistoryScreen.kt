package com.gps.gyment.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.Exercise
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val exercises = remember { mutableStateListOf<Exercise>() }
    val groupedExercises = remember { mutableStateMapOf<String, List<Exercise>>() }

    fun formatDateTimeToDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    fun formatDateTimeToTime(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("HH:mm")

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    if (currentUser != null) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .collection("exercises")
            .whereEqualTo("done", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                exercises.clear()
                for (document in querySnapshot) {
                    val exercise = Exercise(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        repetitions = document.getString("repetitions") ?: "",
                        sets = document.getString("sets") ?: "",
                        muscleGroup = document.getString("muscleGroup") ?: "",
                        createdAt = document.getString("created_at") ?: "",
                        done = document.getBoolean("done") ?: false,
                        doneAt = document.getString("done_at") ?: ""
                    )

                    //exercise.id = document.id
                    exercises.add(exercise)
                }

                groupedExercises.clear()
                groupedExercises.putAll(
                    exercises.groupBy{ formatDateTimeToDate(it.doneAt) }
                )

            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Histórico de Exercícios")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            groupedExercises.forEach { (date, exercises) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    exercises.forEach { exercise ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatDateTimeToTime(exercise.doneAt),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

