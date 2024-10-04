import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.User

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
}
