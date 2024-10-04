import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun formatDateTimeToDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    fun formatDateTimeToTime(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.US)

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    suspend fun fetchDoneExercises(): Map<String, List<Exercise>> {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            try {
                val querySnapshot = db.collection("users")
                    .document(currentUser.uid)
                    .collection("exercises")
                    .whereEqualTo("done", true)
                    .get()
                    .await()

                val exercises = mutableListOf<Exercise>()
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
                    exercises.add(exercise)
                }

                // Agrupar os exercícios por data formatada
                return exercises.groupBy { formatDateTimeToDate(it.doneAt) }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return emptyMap()
    }
}