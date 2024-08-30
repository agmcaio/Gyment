package com.gps.gyment.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.R
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.data.models.Exercise
import com.gps.gyment.ui.components.ExerciseCard
import com.gps.gyment.ui.components.MuscleFilter
import com.gps.gyment.ui.theme.GymentTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf<Muscle?>(null) }
    val exercises = remember { mutableStateListOf<Exercise>() }

    // Se o usuário estiver autenticado, buscar o nome do Firestore
    if (currentUser != null) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userName = document.getString("name") ?: "Usuário"
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

        // Fetch exercises from Firestore
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .collection("exercises")
            .get()
            .addOnSuccessListener { querySnapshot ->
                exercises.clear()
                for (document in querySnapshot) {
                    val exercise = document.toObject(Exercise::class.java)
                    exercise.id = document.id
                    // Filtrar apenas os exercícios que não foram feitos
                    if (!exercise.done) {
                        exercises.add(exercise)
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_exercise") }) {
                Icon(Icons.Filled.Add, contentDescription = "Notifications")
            }
        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 32.dp),
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
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Botão de notificações",
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MuscleFilter(selectedMuscle) { muscle ->
                selectedMuscle = muscle!!
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
                    Text(text = "${exercises.size}")
                }

                exercises.forEach { exercise ->
                    ExerciseCard(exercise = exercise) {
                        navController.navigate("exercise_detail/${exercise.id}")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
