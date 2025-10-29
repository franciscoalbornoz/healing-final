package com.example.healing.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healing.R
import com.example.healing.viewmodel.MedicationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import com.example.healing.navigation.Route
// 👇 AGREGADOS
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.healing.notifications.ReminderScheduler
// 👇 NUEVO para detectar pérdida de foco y formatear 2 dígitos
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun MedicationEditorScreen(
    navController: NavController,
    vm: MedicationViewModel,
    epochDay: Long
) {
    val bg = Color(0xFF92BEAB)
    val card = Color(0xFF1F1432)

    val list by vm.medsOfDay.collectAsState(initial = emptyList())
    LaunchedEffect(epochDay) { vm.selectDay(epochDay) }

    val date = LocalDate.ofEpochDay(epochDay)
    val fmt = DateTimeFormatter.ofPattern("d 'de' MMM", java.util.Locale.getDefault())

    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("1") }
    var hour by remember { mutableStateOf("8") }
    var minute by remember { mutableStateOf("00") }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    val tfColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.White,
        unfocusedIndicatorColor = Color.White,
        cursorColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mi Agenda", color = Color.White)
            Text(date.format(fmt), color = Color.White)
        }
        Spacer(Modifier.height(12.dp))

        Surface(
            color = card,
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 460.dp) // un poco más grande
        ) {
            Column(
                Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra: editar - agregar - borrar
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = {
                        // precarga el último para editar rápido
                        list.lastOrNull()?.let { last ->
                            name = last.name
                            dose = last.doseCount.toString()
                            hour = last.hour.toString()
                            minute = last.minute.toString()
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_editar),
                            contentDescription = "Editar"
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val n = name.trim().ifEmpty { "Medicamento" }.take(40)
                                    val d = dose.toIntOrNull()?.coerceIn(1, 99) ?: 1
                                    val h = hour.toIntOrNull()?.coerceIn(0, 23) ?: 8
                                    val m = minute.toIntOrNull()?.coerceIn(0, 59) ?: 0

                                    // Guarda en DB (tu lógica existente)
                                    vm.add(n, d, epochDay, h, m)

                                    // 👇 programa notificación con WorkManager + sonido raw/recuerda.mp3
                                    ReminderScheduler.scheduleWithWork(
                                        context = context,
                                        epochDay = epochDay,
                                        hour = h,
                                        minute = m,
                                        title = n,
                                        dose = d
                                    )
                                } catch (_: Exception) {
                                    // ignorar
                                } finally {
                                    // volver sí o sí al calendario
                                    val wentBack = navController.popBackStack()
                                    if (!wentBack) {
                                        navController.navigate(Route.MedsCalendar.route) {
                                            launchSingleTop = true
                                            popUpTo(Route.MedsCalendar.route) { inclusive = false }
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(50)
                    ) { Text("Agregar") }

                    IconButton(onClick = {
                        scope.launch {
                            try {
                                list.lastOrNull()?.let { med -> vm.delete(med) }
                            } catch (_: Exception) {
                            } finally {
                                val wentBack = navController.popBackStack()
                                if (!wentBack) {
                                    navController.navigate(Route.MedsCalendar.route) {
                                        launchSingleTop = true
                                        popUpTo(Route.MedsCalendar.route) { inclusive = false }
                                    }
                                }
                            }
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_borrar),
                            contentDescription = "Borrar"
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_pastillas),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 40) name = it },
                    label = { Text("Nombre de medicamento", color = Color.White) },
                    singleLine = true,
                    colors = tfColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    "Agendado para el día ${date.dayOfMonth} de ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = Color.White
                )

                Spacer(Modifier.height(10.dp))


                fun clampDigits2(raw: String, max: Int): String {
                    val d = raw.filter(Char::isDigit).take(2)
                    if (d.isEmpty()) return ""
                    val n = d.toInt()
                    return if (n > max) max.toString() else d
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // --- Tomar # (1..99) ---
                    OutlinedTextField(
                        value = dose,
                        onValueChange = { input ->
                            val d = input.filter(Char::isDigit).take(2)
                            dose = when {
                                d.isEmpty() -> ""
                                d.toInt() < 1 -> "1"
                                else -> d
                            }
                        },
                        label = { Text("Tomar #", color = Color.White) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = tfColors,
                        modifier = Modifier.width(110.dp)
                    )


                    OutlinedTextField(
                        value = hour,
                        onValueChange = { input -> hour = clampDigits2(input, 23) },
                        label = { Text("Hora", color = Color.White) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = tfColors,
                        modifier = Modifier
                            .width(100.dp)
                            .onFocusChanged { st ->
                                if (!st.isFocused && hour.isNotBlank()) {
                                    hour = "%02d".format(hour.toInt().coerceIn(0, 23))
                                }
                            }
                    )

                    // --- Min (0..59) ---
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { input -> minute = clampDigits2(input, 59) },
                        label = { Text("Min", color = Color.White) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = tfColors,
                        modifier = Modifier
                            .width(100.dp)
                            .onFocusChanged { st ->
                                if (!st.isFocused && minute.isNotBlank()) {
                                    minute = "%02d".format(minute.toInt().coerceIn(0, 59))
                                }
                            }
                    )
                }


                Spacer(Modifier.height(18.dp))


                list.forEach { med ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Descripción
                        Text(
                            "${med.name} • ${"%02d:%02d".format(med.hour, med.minute)} • x${med.doseCount}",
                            color = Color.White
                        )


                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Surface(
                                shape = CircleShape,
                                color = if (med.taken) Color(0xFF66BB6A) else Color.LightGray,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text("✓", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }


                            IconButton(onClick = {
                                name = med.name
                                dose = med.doseCount.toString()
                                hour = "%02d".format(med.hour)
                                minute = "%02d".format(med.minute)
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_editar),
                                    contentDescription = "Editar este medicamento"
                                )
                            }


                            IconButton(onClick = {
                                scope.launch { vm.delete(med) }
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_borrar),
                                    contentDescription = "Borrar este medicamento"
                                )
                            }
                        }
                    }
                }

            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF626699)),
            shape = CircleShape
        ) { Text("Volver", color = Color.White) }
    }
}
