package com.example.healing.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val apiKey = "AIzaSyCHlKGQzxgptce8S6WflUXJ7KMlhkbIiGA"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    val messages = mutableStateListOf<ChatMessage>()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        messages.add(ChatMessage(userText, true))

        viewModelScope.launch {
            try {
                val contexto =
                    "Eres un asistente amigable de salud llamado Healing. " +
                            "Responde en español, breve, claro y con consejos útiles."

                val respuesta = generativeModel.generateContent("$contexto\nUsuario: $userText")

                val textoAI = respuesta.text ?: "No pude generar respuesta."

                messages.add(ChatMessage(textoAI, false))

            } catch (e: Exception) {
                messages.add(ChatMessage("Error: ${e.localizedMessage}", false))
            }
        }
    }
}
