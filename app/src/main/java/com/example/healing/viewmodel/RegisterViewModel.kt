package com.example.healing.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.healing.model.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v) }; validate() }
    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v) }; validate() }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v) }; validate() }

    private fun validate() {
        val s = _uiState.value

        val nameErr = if (s.name.isBlank()) "Ingresa tu nombre" else null

        val emailErr = when {
            s.email.isBlank() -> "Ingresa tu correo"
            !isValidEmail(s.email.trim()) -> "Correo inválido "
            else -> null
        }

        val passErr = when {
            s.password.isBlank() -> "Ingresa tu contraseña "
            s.password.length < 6 -> "Mínimo 6 caracteres"
            s.password.length > 15 -> "Máximo 15 caracteres"
            else -> null
        }

        _uiState.update {
            it.copy(
                nameError = nameErr,
                emailError = emailErr,
                passwordError = passErr,
                isValid = (nameErr == null && emailErr == null && passErr == null)
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
