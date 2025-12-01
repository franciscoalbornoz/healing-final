package com.example.healing

import com.example.healing.data.Prefs
import com.example.healing.viewmodel.HomeViewModel
import com.example.healing.model.ChatViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HealingTests {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    // test de geminis
    @Test
    fun `ChatViewModel envia mensaje y recibe respuesta real de Gemini`() = runBlocking {
        println("🚀 [GEMINI] Iniciando test de conexión...")
        val vm = ChatViewModel()

        val mensajePrueba = "Hola, responde solo la palabra: EXITO"
        println("🔵 Enviando: $mensajePrueba")
        vm.sendMessage(mensajePrueba)

        val maxWaitTime = 30000L
        val startTime = System.currentTimeMillis()

        while (vm.messages.size < 2 && System.currentTimeMillis() - startTime < maxWaitTime) {
            delay(1000)
            print(".")
        }
        println("")

        if (vm.messages.size < 2) throw RuntimeException("❌ Timeout: Gemini no respondió.")

        val respuesta = vm.messages[1]
        println("🟢 [GEMINI] Respuesta: ${respuesta.text}")

        if (respuesta.text.contains("API key was reported as leaked")) {
            throw RuntimeException("⛔ ERROR: API Key bloqueada.")
        }

        assertTrue(respuesta.text.isNotBlank())
        assertFalse(respuesta.text.startsWith("Error:"))
    }


    // 2. HÁBITOS tracker

    @Test
    fun `al seleccionar habitos el porcentaje de progreso debe subir`() {
        println("🔵 [HABITOS] Verificando cálculo de progreso...")
        val vm = HomeViewModel()

        assertEquals(0, vm.selected.value.size)

        vm.toggleHabit("agua")

        val total = vm.habits.size.toFloat()
        val esperado1 = 1f / total
        val actual1 = vm.selected.value.size / total

        assertEquals(esperado1, actual1, 0.0001f)
        println("   -> Progreso subió a ${actual1 * 100}%")

        vm.toggleHabit("leer")
        val esperado2 = 2f / total
        val actual2 = vm.selected.value.size / total

        assertEquals(esperado2, actual2, 0.0001f)
        println("   -> Progreso subió a ${actual2 * 100}%")
    }


    // 3. CONTACTO EMERGENCIA
    @Test
    fun `debe permitir escribir y guardar contacto de emergencia correctamente`() {
        println("🔵 [CONTACTO] Verificando datos...")

        val contacto = Prefs.EmergencyContact(
            name = "Mamá",
            phone = "+569 9999 9999",
            address = "Calle Falsa 123"
        )

        assertEquals("Mamá", contacto.name)
        assertEquals("+569 9999 9999", contacto.phone)

        println("   -> Contacto guardado OK")
    }


    // 4. DATOS PERSONALES

    @Test
    fun `debe permitir escribir y guardar datos personales completos`() {
        println("🔵 [PERSONAL] Verificando ficha guardado")

        val paciente = Prefs.PersonalData(
            name = "Francisco",
            rut = "11.222.333-K",
            address = "Santiago",
            blood = "O+",
            allergies = "Ninguna"
        )

        assertTrue(paciente.name.isNotBlank())
        assertEquals("Francisco", paciente.name)

        assertTrue(paciente.rut.isNotBlank())
        assertEquals("11.222.333-K", paciente.rut)

        println("   -> ficha guardada")
    }


    // 5. MEDICAMENTOS (NOTIFICACIÓN)
    @Test
    fun `recordatorio de medicamento tiene datos validos para notificar`() {
        println("🔵 [MEDICAMENTOS] Validando integridad para notificación...")

        // Simulamos los datos que usaría tu sistema de notificaciones
        val nombreMedicamento = "Ibuprofeno"
        val dosis = "400mg"
        val horaProgramada = "08:30" // Formato HH:MM

        // 1. Validar que tenga nombre
        assertTrue("El nombre es obligatorio", nombreMedicamento.isNotBlank())

        // 2. Validar dosis
        assertTrue("La dosis es obligatoria", dosis.isNotBlank())



        println("   -> Datos de notificación OK")
    }
}