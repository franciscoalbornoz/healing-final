package com.example.healing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healing.data.Prefs
import com.example.healing.data.Prefs.EmergencyContact
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.foundation.clickable

@Composable
fun EmergencyContactScreen(navController: NavController) {
    val bg = Color(0xFFA8D5BA)
    val card = Color(0xFFA8D5BA)
    val title = Color(0xFF2E235E)

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val prefs = remember { Prefs(context) }
    val contact by prefs.emergencyContactFlow.collectAsState(initial = null)


    var showEditor by remember { mutableStateOf(false) }
    var name by remember(contact) { mutableStateOf(contact?.name ?: "") }
    var phone by remember(contact) { mutableStateOf(contact?.phone ?: "") }
    var addr by remember(contact) { mutableStateOf(contact?.address ?: "") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mi Agenda", fontSize = 16.sp, color = Color.DarkGray)
            Text("14:20 p. m.\nJueves 25 de sept", fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.End)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Contacto de\nemergencia",
            color = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))

        Surface(color = card, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Nombre:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (contact?.name.isNullOrBlank()) "—" else contact!!.name, color = title)
                Spacer(Modifier.height(10.dp))
                Text("numero de\ncelular:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                val phoneShown = if (contact?.phone.isNullOrBlank()) "—" else contact!!.phone
                if (phoneShown == "—") {
                    Text("—", color = title)
                } else {
                    Text(
                        text = phoneShown,
                        color = title,
                        modifier = Modifier.clickable {
                            clipboard.setText(AnnotatedString(phoneShown))
                            Toast.makeText(context, "Número copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text("donde vive:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (contact?.address.isNullOrBlank()) "—" else contact!!.address, color = title)
            }
        }

        Spacer(Modifier.height(20.dp))


        if (contact == null) {
            Button(
                onClick = { showEditor = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF63918B))
            ) { Text("agregar contacto", color = Color.Black) }
        } else {
            Button(
                onClick = { showEditor = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF63918B))
            ) { Text("editar contacto", color = Color.Black) }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Regresar", color = Color.DarkGray) }
    }


    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text(if (contact == null) "Agregar contacto" else "Editar contacto", color = title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = name,  onValueChange = { if (it.length <= 40) name = it },  label = { Text("Nombre") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { if (it.length <= 40) phone = it }, label = { Text("Número de celular") }, singleLine = true)
                    OutlinedTextField(value = addr,  onValueChange = { if (it.length <= 40) addr = it },  label = { Text("Dónde vive") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val c = EmergencyContact(name.trim(), phone.trim(), addr.trim())
                    scope.launch { prefs.saveEmergencyContact(c) }
                    showEditor = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text("Cancelar") } }
        )
    }
}
