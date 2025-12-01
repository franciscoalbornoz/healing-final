package com.example.healing.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.example.healing.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

// 👇 NUEVO
import androidx.compose.ui.platform.LocalContext
import com.example.healing.data.Prefs
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()


    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()

    var showHeader by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(120)
        showHeader = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCA9BFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
                exit  = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_healing),
                        contentDescription = "Logo Healing",
                        modifier = Modifier.size(180.dp)
                    )
                    Spacer(Modifier.height(19.dp))
                    Text(
                        text = "Crear cuenta",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = vm::onNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null,
                supportingText = {
                    state.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(Modifier.height(16.dp))

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

            OutlinedTextField(
                value = state.password,
                onValueChange = { if (it.length <= 25) vm.onPasswordChange(it) }, // 👈 límite
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

            Button(
                onClick = {
                    vm.submit(
                        onSuccess = {
                            // 👇 NUEVO: guardar usuario local y volver a login
                            scope.launch {
                                prefs.saveUser(
                                    Prefs.User(
                                        name = state.name.trim(),
                                        email = state.email.trim(),
                                        password = state.password // ya validado por tu VM
                                    )
                                )
                                onBackToLogin()
                            }
                        },
                        onFail = { /* ya muestras los errores con isError/supportingText */ }
                    )
                },
                enabled = state.isValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    disabledContainerColor = Color.DarkGray.copy(alpha = 0.4f)
                )
            ) { Text("Registrar", color = Color.Black) }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onBackToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color.Black)
            }
        }
    }
}
