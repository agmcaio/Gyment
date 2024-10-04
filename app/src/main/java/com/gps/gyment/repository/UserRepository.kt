import androidx.navigation.NavController
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
                    val city = document.getString("cidade") ?: "Não informado"
                    val neighborhood = document.getString("bairro") ?: "Não informado"
                    val street = document.getString("rua") ?: "Não informado"
                    val cep = document.getString("cep") ?: "Não informado"
                    Result.success(UserProfile(name, email,city,cep,neighborhood,street))
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

    suspend fun registerUser(name: String, email: String, password: String, userType: String,cep:String,cidade:String,bairro:String,rua:String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "userType" to userType,
                    "bairro" to bairro,
                    "cidade" to cidade,
                    "cep" to cep,
                    "rua" to rua,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users").document(user.uid).set(userData).await()
                Result.success("Usuário criado com sucesso")
            } ?: Result.failure(Exception("Erro ao criar usuário"))
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
    val email: String,
    val cidade :String,
    val cep : String,
    val bairro : String,
    val rua : String
)
