import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _fetchedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedExercises: StateFlow<List<Exercise>> = _fetchedExercises

    private val _filteredExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

    var selectedMuscle: Muscle? = null

    init {
        fetchUserData()
        fetchExercisesRealtime()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userName.value = repository.fetchUserName()
        }
    }

    private fun fetchExercisesRealtime() {
        repository.fetchExercisesRealtime(
            onSuccess = { exercises ->
                _fetchedExercises.value = exercises
                updateFilteredExercises()
            },
            onError = { e -> Log.w("HomeViewModel", "Error fetching exercises", e) }
        )
    }

    fun onMuscleSelected(muscle: Muscle?) {
        selectedMuscle = muscle
        updateFilteredExercises()
    }

    private fun updateFilteredExercises() {
        _filteredExercises.value = if (selectedMuscle != null) {
            _fetchedExercises.value.filter { it.muscleGroup == selectedMuscle!!.name }
        } else {
            _fetchedExercises.value
        }
    }
}
