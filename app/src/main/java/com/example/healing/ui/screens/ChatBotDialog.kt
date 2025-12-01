package com.example.healing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow // <--- IMPORTANTE
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.healing.model.ChatViewModel

@Composable
fun ChatBotDialog(
    onDismiss: () -> Unit,
    viewModel: ChatViewModel
) {
    var text by remember { mutableStateOf("") }

    // Colores
    val bgDialog = Color(0xFFF5ECFF)
    val headerBg = Color(0xFFCA9BFF)
    val userBubble = Color(0xFF63918B)
    val aiBubble = Color(0xFF9C82D6)
    val inputBg = Color(0xFFE5D9FF)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp),
            colors = CardDefaults.cardColors(containerColor = bgDialog),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER CORREGIDO ----------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(headerBg, Color(0xFFB68CFF))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤖", fontSize = 18.sp)
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // 👇 AQUÍ ESTÁ EL ARREGLO
                        // Usamos weight(1f) para que el texto ocupe el espacio disponible
                        // sin empujar ni aplastar al botón de cerrar.
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Asistente Healing",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Consejos breves de salud y bienestar",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Espacio fijo pequeño antes del botón
                        Spacer(Modifier.width(4.dp))

                        // Botón Cerrar: Se mantiene a su tamaño natural
                        TextButton(
                            onClick = onDismiss,
                            // ContentPadding pequeño para que no ocupe tanto espacio extra
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                "Cerrar",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // ---------- LISTA DE MENSAJES ----------
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    reverseLayout = false
                ) {
                    items(viewModel.messages) { msg ->
                        ChatBubble(
                            text = msg.text,
                            isUser = msg.isUser,
                            userColor = userBubble,
                            aiColor = aiBubble
                        )
                    }
                }

                // ---------- INPUT ----------
                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp,
                    color = bgDialog,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(inputBg),
                            placeholder = {
                                Text(
                                    "Pregunta algo…",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.width(6.dp))

                        FilledIconButton(
                            onClick = {
                                if (text.isNotBlank()) {
                                    viewModel.sendMessage(text)
                                    text = ""
                                }
                            },
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = headerBg,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    userColor: Color,
    aiColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) userColor else aiColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp
            ),
            modifier = Modifier.widthIn(max = 260.dp),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (isUser) Color.White else Color(0xFF1E1230),
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}