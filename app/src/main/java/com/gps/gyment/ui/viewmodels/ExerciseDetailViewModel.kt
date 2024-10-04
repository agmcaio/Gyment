package com.gps.gyment.ui.viewmodels

import ExerciseRepository
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExerciseDetailViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {
    var exercise = mutableStateOf<Exercise?>(null)
        private set

    var isDone = mutableStateOf(false)
        private set

    fun fetchExercise(exerciseId: String) {
        repository.getExercise(
            exerciseId,
            onSuccess = { result ->
                exercise.value = result
                isDone.value = exercise.value?.done ?: false
            },
            onError = { exception -> exception.printStackTrace() }
        )
    }

    fun markAsDone(exerciseId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        repository.markExerciseAsDone(exerciseId, onSuccess, onError)
    }

    fun deleteExercise(exerciseId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        repository.deleteExercise(
            exerciseId,
            onSuccess = { onSuccess() },
            onError = { e -> onError(e) }
        )
    }

    fun updateExercise(exerciseId: String, updatedData: HashMap<String, Any?>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateExercise(
                exerciseId,
                updatedData,
                onSuccess,
                onError
            )
        }
    }
}
