package com.example.labelly_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labelly_application.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Verifică dacă utilizatorul este deja logat
        _uiState.value = _uiState.value.copy(
            isLoggedIn = authRepository.isUserLoggedIn()
        )
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email și parola sunt obligatorii"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        successMessage = "Autentificare reușită!"
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }

    fun register(email: String, password: String, username: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Toate câmpurile sunt obligatorii"
            )
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Parola trebuie să aibă cel puțin 6 caractere"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.register(email, password, username)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        successMessage = "Cont creat cu succes!"
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email-ul este obligatoriu"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Email de resetare trimis!"
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        authRepository.logout()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            successMessage = "Deconectare reușită!"
        )
        onSuccess()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("password") == true -> "Parolă incorectă"
            exception.message?.contains("email") == true -> "Email invalid"
            exception.message?.contains("user-not-found") == true -> "Utilizatorul nu există"
            exception.message?.contains("email-already-in-use") == true -> "Email-ul este deja folosit"
            exception.message?.contains("weak-password") == true -> "Parola este prea slabă"
            exception.message?.contains("network") == true -> "Problemă de rețea"
            else -> "A apărut o eroare: ${exception.message}"
        }
    }
}