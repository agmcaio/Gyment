import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(
    private val repository: HistoryRepository = HistoryRepository()
) : ViewModel() {

    val exercises = mutableStateListOf<Exercise>()
    val groupedExercises = mutableStateMapOf<String, List<Exercise>>()

    fun fetchDoneExercises() {
        viewModelScope.launch {
            val fetchedExercises = repository.fetchDoneExercises()
            groupedExercises.clear()
            groupedExercises.putAll(fetchedExercises)

            exercises.clear()
            fetchedExercises.values.flatten().let {
                exercises.addAll(it)
            }
        }
    }

    fun formatDateTimeToTime(dateString: String): String {
        return repository.formatDateTimeToTime(dateString)
    }
}
