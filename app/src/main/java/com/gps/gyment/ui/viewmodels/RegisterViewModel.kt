
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var userType: String = "aluno"
    var isLoading: Boolean = false
    var nameError: String? = null
    var emailError: String? = null
    var passwordError: String? = null
    var confirmPasswordError: String? = null

    fun registerUser(onComplete: () -> Unit, onError: (String) -> Unit) {
        nameError = if (name.isEmpty()) "Nome não pode ser vazio" else null
        emailError = if (email.isEmpty()) "E-mail não pode ser vazio" else null
        passwordError = if (password.isEmpty()) "Senha não pode ser vazia" else null
        confirmPasswordError = if (confirmPassword.isEmpty()) "Confirme sua senha" else null

        if (password != confirmPassword) {
            confirmPasswordError = "As senhas não correspondem"
        }

        if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            isLoading = true
            viewModelScope.launch {
                val result = userRepository.registerUser(name, email, password, userType)
                isLoading = false
                if (result.isSuccess) {
                    onComplete()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Erro ao criar usuário")
                }
            }
        }
    }
}