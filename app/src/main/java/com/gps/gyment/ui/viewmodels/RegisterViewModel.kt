
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var cep by mutableStateOf("")
    var bairro by mutableStateOf("")
    var cidade by mutableStateOf("")
    var rua by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var userType by mutableStateOf("aluno")
    var isLoading by mutableStateOf(false)
    var nameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

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
                val result = userRepository.registerUser(name, email, password, userType,cep,cidade, bairro, rua)
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