package com.gps.gyment.ui.viewmodels

import ExerciseRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExerciseDetailViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {
    var exercise: Exercise? = null
        private set

    var isDone: Boolean = false
        private set

    fun fetchExercise(exerciseId: String) {
        repository.getExercise(
            exerciseId,
            onSuccess = { result ->
                exercise = result
                isDone = exercise?.done ?: false
            },
            onError = { exception -> exception.printStackTrace() }
        )
    }

    fun markAsDone(exerciseId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        repository.markExerciseAsDone(exerciseId, onSuccess, onError)
    }
}
