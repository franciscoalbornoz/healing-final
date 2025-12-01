package com.example.healing.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healing.R
import com.example.healing.viewmodel.LoginViewModel

// 👇 NUEVO
import androidx.compose.ui.platform.LocalContext
import com.example.healing.data.Prefs
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {},
    vm: LoginViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()


    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()
    var loginError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9B7CDA)), // gris verdoso de fondo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Texto Healing arriba
            Text(
                text = "Healing",
                fontSize = 70.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            // Logo debajo del texto
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_healing),
                contentDescription = "Logo Healing",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Subtítulo
            Text(
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo correo
            OutlinedTextField(
                value = state.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Campo contraseña
            OutlinedTextField(
                value = state.password,
                onValueChange = { if (it.length <= 25) vm.onPasswordChange(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = state.passwordError != null,
                supportingText = {
                    state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(Modifier.height(24.dp))

            // Botón Ingresar
            Button(
                onClick = {
                    scope.launch {
                        val user = prefs.userFlow.firstOrNull()
                        if (user != null &&
                            user.email.trim() == state.email.trim() &&
                            user.password == state.password
                        ) {
                            prefs.setLoggedIn(true)
                            onLogin() // ✅ entra al Home
                        } else {
                            loginError = "Correo o contraseña incorrectos"
                        }
                    }
                },
                enabled = state.isValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    disabledContainerColor = Color.DarkGray.copy(alpha = 0.4f)
                )
            ) {
                Text("Ingresar", color = Color.White)
            }

            // 👇 Mostrar error si no coincide
            if (loginError != null) {
                Spacer(Modifier.height(8.dp))
                Text(loginError ?: "", color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Texto de registro
            TextButton(onClick = onRegister) {
                Text("¿No tienes cuenta? Regístrate", color = Color.Black)
            }
        }
    }
}
