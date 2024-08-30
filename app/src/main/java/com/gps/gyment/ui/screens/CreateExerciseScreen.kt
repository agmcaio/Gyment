package com.gps.gyment.ui.screens

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.enums.Muscle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(navController: NavController) {
    val context = LocalContext.current

    // State variables to store user input
    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var repetitions by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf(Muscle.CHEST) } // Default selection


    // Function to add exercise to Firestore
    fun addExercise() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val exercise = hashMapOf(
                "name" to name,
                "sets" to sets,
                "repetitions" to repetitions,
                "muscle_group" to selectedMuscle.name,
                "done" to false,
                "created_at" to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .collection("exercises")
                .add(exercise)
                .addOnSuccessListener {
                    Toast.makeText(context, "Exercício criado com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    name = ""
                    sets = ""
                    repetitions = ""
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(context, "Falha ao criar exercício", Toast.LENGTH_SHORT).show()
                }
        }
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
                },
            )
        }
    ) { paddingValues ->
        // UI Layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sets,
                onValueChange = { sets = it },
                label = { Text("Quantidade de séries") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = repetitions,
                onValueChange = { repetitions = it },
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
                    value = selectedMuscle.displayName,
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
                                selectedMuscle = muscle
                                expanded = false
                            },
                            text = { Text(muscle.displayName) }
                        )
                    }
                }
            }

            Button(
                onClick = { addExercise() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar")
            }
        }
    }
}
