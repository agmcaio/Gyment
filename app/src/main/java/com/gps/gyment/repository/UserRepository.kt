import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(): Result<UserProfile> {
        val currentUser = auth.currentUser
        return try {
            if (currentUser != null) {
                val document = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (document.exists()) {
                    val name = document.getString("name") ?: "Usuário"
                    val email = currentUser.email ?: ""
                    Result.success(UserProfile(name, email))
                } else {
                    Result.failure(Exception("Usuário não encontrado"))
                }
            } else {
                Result.failure(Exception("Usuário não autenticado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}

data class UserProfile(
    val name: String,
    val email: String
)
