import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.data.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _students = MutableLiveData<List<User>>()
    val students: LiveData<List<User>> get() = _students

    var name = mutableStateOf("")
    var sets = mutableStateOf("")
    var repetitions = mutableStateOf("")
    var selectedMuscle = mutableStateOf(Muscle.CHEST)
    var selectedStudentId = mutableStateOf("")

    fun fetchStudents() {
        repository.fetchStudents(
            onSuccess = { _students.value = it },
            onError = { e -> Log.w(TAG, "Error fetching students", e) }
        )
    }

    fun addExercise() {
        val exercise = hashMapOf<String, Any?>(
            "name" to name.value,
            "sets" to sets.value.toIntOrNull(),
            "repetitions" to repetitions.value.toIntOrNull(),
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
            },
            onError = {
                it.printStackTrace()
            }
        )
    }
}
