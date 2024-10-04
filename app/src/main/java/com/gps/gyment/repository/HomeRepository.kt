import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gps.gyment.data.models.Exercise
import kotlinx.coroutines.tasks.await

class HomeRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
    suspend fun fetchUserName(): String {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return try {
                val document = db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                document.getString("name") ?: "Usuário"
            } catch (e: Exception) {
                e.printStackTrace()
                "Usuário"
            }
        }
        return "Usuário"
    }

    suspend fun fetchExercises(): List<Exercise> {
        val currentUser = auth.currentUser
        val exercisesList = mutableListOf<Exercise>()

        if (currentUser != null) {
            try {
                val querySnapshot = db.collection("users")
                    .document(currentUser.uid)
                    .collection("exercises")
                    .get()
                    .await()

                for (document in querySnapshot) {
                    val exercise = document.toObject(Exercise::class.java)
                    exercise.id = document.id
                    if (!exercise.done) {
                        exercisesList.add(exercise)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return exercisesList
    }
}
