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

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _filteredExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val filteredExercises: StateFlow<List<Exercise>> = _filteredExercises

    var selectedMuscle: Muscle? = null

    init {
        fetchUserData()
        fetchExercises()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userName.value = repository.fetchUserName()
        }
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            val exercisesList = repository.fetchExercises()
            _exercises.value = exercisesList
            updateFilteredExercises()
        }
    }

    fun onMuscleSelected(muscle: Muscle?) {
        selectedMuscle = muscle
        updateFilteredExercises()
    }

    private fun updateFilteredExercises() {
        _filteredExercises.value = if (selectedMuscle != null) {
            _exercises.value.filter { it.muscleGroup == selectedMuscle!!.name }
        } else {
            _exercises.value
        }
    }
}
