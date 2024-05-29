import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adiandroid.storyscape.data.api.ApiService
import com.adiandroid.storyscape.ui.auth.LoginViewModel

class LoginViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}