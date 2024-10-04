import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.data.models.Exercise
import com.gps.gyment.data.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _students = MutableLiveData<List<User>>()

    var name = mutableStateOf("")
    var sets = mutableStateOf("")
    var repetitions = mutableStateOf("")
    var selectedMuscle = mutableStateOf(Muscle.CHEST)
    var selectedStudentId = mutableStateOf("")
    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises


    fun fetchStudents() {
        repository.fetchStudents(
            onSuccess = { _students.value = it },
            onError = { e -> Log.w(TAG, "Error fetching students", e) }
        )
    }

    fun fetchExercises() {
        repository.fetchExercises(
            onSuccess = { _exercises.value = it },
            onError = { e -> Log.w(TAG, "Error fetching exercises", e) }
        )
    }


    fun addExercise(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val exercise = hashMapOf<String, Any?>(
            "name" to name.value,
            "sets" to sets.value,
            "repetitions" to repetitions.value,
            "muscle_group" to selectedMuscle.value,
            "done" to false,
            "created_at" to SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            "done_at" to null
        )

        repository.addExercise(exercise,
            onSuccess = {
                name.value = ""
                sets.value = ""
                repetitions.value = ""
                selectedStudentId.value = ""
                onSuccess()
                fetchStudents()
                fetchExercises()
            },
            onError = { e ->
                onError(e)
            }
        )
    }
}
