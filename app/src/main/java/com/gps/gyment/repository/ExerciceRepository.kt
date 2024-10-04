import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.Exercise
import com.gps.gyment.data.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
    fun fetchStudents(onSuccess: (List<User>) -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .whereEqualTo("usertype", "aluno")
                .get()
                .addOnSuccessListener { documents ->
                    val students = documents.map { document ->
                        User(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            userType = document.getString("usertype") ?: ""
                        )
                    }
                    onSuccess(students)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }

    fun addExercise(exercise: HashMap<String, Any?>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("exercises")
                .add(exercise)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }
    }

    fun getExercise(exerciseId: String, onSuccess: (Exercise?) -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("exercises")
                .document(exerciseId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val exercise = Exercise(
                            id = document.getString("id") ?: "0",
                            name = document.getString("name") ?: "",
                            repetitions = document.getString("repetitions") ?: "",
                            sets = document.getString("sets") ?: "",
                            muscleGroup = document.getString("muscle_group") ?: "",
                            createdAt = document.getString("created_at") ?: "",
                            done = document.getBoolean("done") ?: false,
                            doneAt = document.getString("done_at") ?: ""
                        )
                        onSuccess(exercise)  // Passa o objeto Exercise recuperado
                    }
                }
                .addOnFailureListener { onError(it) }
        }
    }

    fun markExerciseAsDone(exerciseId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("exercises")
                .document(exerciseId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.getBoolean("done") == false) {
                        db.collection("users")
                            .document(user.uid)
                            .collection("exercises")
                            .document(exerciseId)
                            .update(
                                mapOf(
                                    "done" to true,
                                    "done_at" to SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                                )
                            )
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener(onError)
                    }
                }
            }
    }

    fun deleteExercise(exerciseId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("exercises")
                .document(exerciseId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }
    }

    fun updateExercise(exerciseId: String, updatedExercise: HashMap<String, Any?>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("exercises")
                .document(exerciseId)
                .update(updatedExercise)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }
    }
}
