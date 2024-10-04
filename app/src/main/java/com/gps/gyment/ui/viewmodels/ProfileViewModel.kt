import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val result = userRepository.getUserProfile()
            if (result.isSuccess) {
                _userProfile.value = result.getOrNull()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun logout() {
        userRepository.logout()
    }
}
