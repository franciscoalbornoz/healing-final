package com.example.healing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healing.navigation.Route
import com.example.healing.viewmodel.MedicationViewModel
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MedicationCalendarScreen(navController: NavController, vm: MedicationViewModel) {

    val bg = Color(0xFF9C82D6)      // Fondo Púrpura principal
    val title = Color(0xFF2E235E)   // Texto oscuro (Mi Agenda, Mes)
    val buttonColor = Color(0xFF626699) // Color grisáceo/violeta para botón Salir


    var current by remember { mutableStateOf(YearMonth.now()) }

    val start = current.atDay(1).toEpochDay()
    val end = current.atEndOfMonth().toEpochDay()

    val medsThisMonth by remember(start, end) {
        vm.medsBetween(start, end)
    }.collectAsState(initial = emptyList())

    val daysWithMeds = remember(medsThisMonth) {
        medsThisMonth.map { it.dateEpochDay }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg) // Fondo actualizado
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Cambiado a color 'title' para que coincida con las otras pantallas púrpuras
            Text("Mi Agenda", color = title, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(8.dp))
        Text("Calendario", color = title, style = MaterialTheme.typography.headlineSmall)
        Text(
            current.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() },
            color = title, style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { current = current.minusMonths(1) }) { Text("◀", color = title) }
            TextButton(onClick = { current = current.plusMonths(1) }) { Text("▶", color = title) }
        }

        Spacer(Modifier.height(8.dp))
        val week = listOf("L","M","M","J","V","S","D")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Días de la semana en blanco, como en la foto
            week.forEach { Text(it, color = Color.White, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(6.dp))

        val firstDay = current.atDay(1)
        val offset = (firstDay.dayOfWeek.value + 6) % 7 // lunes=0
        val daysInMonth = current.lengthOfMonth()

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            var d = 1
            repeat(6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(7) { col ->
                        if (d == 1 && col < offset || d > daysInMonth) {
                            Spacer(Modifier.size(36.dp))
                        } else {
                            val date = current.atDay(d)
                            val epochDay = date.toEpochDay()
                            DayCell(
                                day = d,
                                hasMed = epochDay in daysWithMeds,
                                onClick = {
                                    vm.selectDay(epochDay)
                                    navController.navigate(Route.MedEditor.create(epochDay))
                                }
                            )
                            d++
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Botón Salir con el color de la foto
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = CircleShape,
            modifier = Modifier.width(150.dp)
        ) { Text("Salir", color = Color.White) }
    }
}

@Composable
private fun DayCell(day: Int, hasMed: Boolean, onClick: () -> Unit) {
    // Colores de las burbujas
    val base = Color(0xFF5C5470)  // Gris oscuro para días normales (como en la foto)
    val medColor = Color(0xFF0F2185) // Rojo para días con medicamentos (según leyenda de la foto)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(if (hasMed) medColor else base, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("$day", color = Color.White)
        }
    }
}