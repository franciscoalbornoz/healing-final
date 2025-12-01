package com.example.healing.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val apiKey = "key"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    val messages = mutableStateListOf<ChatMessage>()

    // Variable para controlar la animación de "Escribiendo..."
    var isTyping by mutableStateOf(false)
        private set

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        // 1. Agregamos el mensaje del usuario
        messages.add(ChatMessage(userText, true))

        // 2. ACTIVAMOS LA BURBUJA (Antes de lanzar la corrutina)
        isTyping = true

        viewModelScope.launch {
            try {
                val contexto =
                    "Eres un asistente amigable de salud llamado Healing. " +
                            "Responde en español, breve, claro y con consejos útiles."

                val respuesta = generativeModel.generateContent("$contexto\nUsuario: $userText")
                val textoAI = respuesta.text ?: "No pude generar respuesta."

                // 3. Agregamos la respuesta de la IA
                messages.add(ChatMessage(textoAI, false))

            } catch (e: Exception) {
                messages.add(ChatMessage("Error: ${e.localizedMessage}", false))
            } finally {
                // 4. DESACTIVAMOS LA BURBUJA (Siempre al final)
                isTyping = false
            }
        }
    }
}