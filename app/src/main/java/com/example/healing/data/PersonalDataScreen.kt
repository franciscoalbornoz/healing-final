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
import com.example.healing.data.Prefs.PersonalData
import kotlinx.coroutines.launch

@Composable
fun PersonalDataScreen(navController: NavController) {
    val bg = Color(0xFFA17CEE)
    val card = Color(0xFFD1D0FB)
    val title = Color(0xFF2E235E)

    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val data by prefs.personalDataFlow.collectAsState(initial = null)

    var showEditor by remember { mutableStateOf(false) }
    var name by remember(data) { mutableStateOf(data?.name ?: "") }
    var rut by remember(data) { mutableStateOf(data?.rut ?: "") }
    var addr by remember(data) { mutableStateOf(data?.address ?: "") }
    var blood by remember(data) { mutableStateOf(data?.blood ?: "") }
    var allergies by remember(data) { mutableStateOf(data?.allergies ?: "") }

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
            "Datos personales",
            color = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))

        Surface(color = card, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Nombre:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (data?.name.isNullOrBlank()) "—" else data!!.name, color = title)
                Spacer(Modifier.height(10.dp))

                Text("Rut:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (data?.rut.isNullOrBlank()) "—" else data!!.rut, color = title)
                Spacer(Modifier.height(10.dp))

                Text("Domicilio", color = title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(if (data?.address.isNullOrBlank()) "—" else data!!.address, color = title)
                Spacer(Modifier.height(10.dp))

                Text("tipo sanguineo:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (data?.blood.isNullOrBlank()) "—" else data!!.blood, color = title)
                Spacer(Modifier.height(10.dp))

                Text("alergias o\nenfermedades:", color = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(if (data?.allergies.isNullOrBlank()) "—" else data!!.allergies, color = title)
            }
        }

        Spacer(Modifier.height(20.dp))

        if (data == null) {
            Button(
                onClick = { showEditor = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4491))
            ) { Text("agregar informacion", color = Color.Black) }
        } else {
            Button(
                onClick = { showEditor = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF63918B))
            ) { Text("editar informacion", color = Color.Black) }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Regresar", color = Color.DarkGray) }
    }

    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text(if (data == null) "Agregar información" else "Editar información", color = title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = name,      onValueChange = { if (it.length <= 40) name = it },      label = { Text("Nombre") }, singleLine = true)
                    OutlinedTextField(value = rut,       onValueChange = { if (it.length <= 40) rut = it },       label = { Text("RUT") }, singleLine = true)
                    OutlinedTextField(value = addr,      onValueChange = { if (it.length <= 40) addr = it },      label = { Text("Domicilio") })
                    OutlinedTextField(value = blood,     onValueChange = { if (it.length <= 40) blood = it },     label = { Text("Tipo sanguíneo") }, singleLine = true)
                    OutlinedTextField(value = allergies, onValueChange = { if (it.length <= 40) allergies = it }, label = { Text("Alergias o enfermedades") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val d = PersonalData(name.trim(), rut.trim(), addr.trim(), blood.trim(), allergies.trim())
                    scope.launch { prefs.savePersonalData(d) }
                    showEditor = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text("Cancelar") } }
        )
    }
}
