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
    val successMessage: String? = null,
    val userInfo: Map<String, Any>? = null
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Verifică dacă utilizatorul este deja logat
        val isLoggedIn = authRepository.isUserLoggedIn()
        _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)

        // Dacă este logat, încarcă informațiile utilizatorului
        if (isLoggedIn) {
            loadUserInfo()
        }
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
                    loadUserInfo()
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

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email-ul nu este valid"
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
                    loadUserInfo()
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

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email-ul nu este valid"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Email de resetare trimis! Verifică-ți emailul."
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
            userInfo = null,
            successMessage = "Deconectare reușită!"
        )
        onSuccess()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            authRepository.getUserInfo()
                .onSuccess { userInfo ->
                    _uiState.value = _uiState.value.copy(userInfo = userInfo)
                }
                .onFailure { exception ->
                    // Nu afișăm eroare pentru încărcarea informațiilor utilizatorului
                    // dar le logăm pentru debugging
                    println("Failed to load user info: ${exception.message}")
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getErrorMessage(exception: Throwable): String {
        val errorMessage = exception.message?.lowercase() ?: ""

        // Adaugă debugging pentru a vedea exact ce eroare primim
        println("🔥 DEBUG Firebase Error: ${exception.message}")

        return when {
            // Erori de parolă
            errorMessage.contains("password is invalid") -> "Parolă incorectă"
            errorMessage.contains("wrong-password") -> "Parolă incorectă"
            errorMessage.contains("invalid-password") -> "Parolă incorectă"
            errorMessage.contains("password") -> "Parolă incorectă"

            // Erori de utilizator inexistent
            errorMessage.contains("user not found") -> "Contul nu există"
            errorMessage.contains("user-not-found") -> "Contul nu există"
            errorMessage.contains("no user record") -> "Contul nu există"

            // Erori de email
            errorMessage.contains("email address is badly formatted") -> "Format email invalid"
            errorMessage.contains("invalid-email") -> "Email invalid"
            errorMessage.contains("email") -> "Email invalid"

            // Erori de înregistrare
            errorMessage.contains("email-already-in-use") -> "Email-ul este deja folosit"
            errorMessage.contains("email is already in use") -> "Email-ul este deja folosit"
            errorMessage.contains("weak-password") -> "Parola trebuie să aibă cel puțin 6 caractere"

            // Erori de rețea
            errorMessage.contains("network") -> "Problemă de conexiune. Verifică internetul"
            errorMessage.contains("timeout") -> "Conexiunea a expirat. Încearcă din nou"

            // Erori de rate limiting
            errorMessage.contains("too-many-requests") -> "Prea multe încercări. Așteaptă câteva minute"
            errorMessage.contains("temporarily disabled") -> "Contul este temporar dezactivat"

            // Eroare generală
            else -> "Eroare: Email sau parolă incorecte"
        }
    }
}