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
    val bg = Color(0xFF92BEAB)
    val title = Color(0xFF2E235E)

    var current by remember { mutableStateOf(YearMonth.now()) }

    val start = current.atDay(1).toEpochDay()
    val end = current.atEndOfMonth().toEpochDay()

    // 👇 NUEVO: días del mes con medicamentos
    val medsThisMonth by remember(start, end) {
        vm.medsBetween(start, end)
    }.collectAsState(initial = emptyList())

    val daysWithMeds = remember(medsThisMonth) {
        medsThisMonth.map { it.dateEpochDay }.toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mi Agenda", color = Color.White, fontWeight = FontWeight.SemiBold)
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
            TextButton(onClick = { current = current.minusMonths(1) }) { Text("◀") }
            TextButton(onClick = { current = current.plusMonths(1) }) { Text("▶") }
        }

        Spacer(Modifier.height(8.dp))
        val week = listOf("L","M","M","J","V","S","D")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            week.forEach { Text(it, color = Color.White) }
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
                                hasMed = epochDay in daysWithMeds, // 👈 ahora pinta el punto
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
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF626699)),
            shape = CircleShape
        ) { Text("Salir", color = Color.White) }
    }
}

@Composable
private fun DayCell(day: Int, hasMed: Boolean, onClick: () -> Unit) {
    val base = Color(0xFF027C68)
    val blue = Color(0xFF3F51B5)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(if (hasMed) blue else base, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("$day", color = Color.White)
        }
    }
}
