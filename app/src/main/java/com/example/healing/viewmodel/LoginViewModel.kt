package com.example.healing.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.healing.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
        validate()
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
        validate()
    }

    private fun validate() {
        val s = _uiState.value
        val emailErr = when {
            s.email.isBlank() -> "Ingresa tu correo"
            !isValidEmail(s.email.trim()) -> "Correo inválido "
            else -> null
        }

        val passwordErr = when {
            s.password.isBlank() -> "Ingresa tu contraseña"
            s.password.length < 6 -> "Mínimo 6 caracteres"
            s.password.length > 15 -> "Máximo 20 caracteres"
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailErr,
                passwordError = passwordErr,
                isValid = (emailErr == null && passwordErr == null)
            )
        }
    }

    fun submit(onSuccess: () -> Unit, onFail: () -> Unit) {
        validate()
        if (_uiState.value.isValid) onSuccess() else onFail()
    }

    private fun isValidEmail(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                (email.endsWith(".com", ignoreCase = true) ||
                        email.endsWith(".cl",  ignoreCase = true))
    }
}
