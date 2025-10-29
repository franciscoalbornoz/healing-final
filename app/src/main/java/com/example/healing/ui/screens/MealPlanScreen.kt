// app/src/main/java/com/example/healing/ui/screens/MealPlanScreen.kt
package com.example.healing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healing.viewmodel.MealPlanViewModel
import com.example.healing.viewmodel.MealPlanViewModel.Companion.days
import com.example.healing.viewmodel.MealPlanViewModel.Companion.meals
import com.example.healing.viewmodel.MealPlanViewModel.Companion.dowToInt

@Composable
fun MealPlanScreen(navController: NavController, vm: MealPlanViewModel) {
    val bg = Color(0xFFA8D5BA)
    val card = Color(0xFFD1D0FB)
    val title = Color(0xFF2E235E)

    val all by vm.allMeals.collectAsState()

    var showAdd by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        // Título
        Text("Plan Alimenticio", color = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { showAdd = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF63918B))
            ) { Text("Agregar Comida", color = Color.Black) }

            Button(
                onClick = { showDelete = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF63918B))
            ) { Text("Eliminar Comida", color = Color.Black) }
        }

        Spacer(Modifier.height(16.dp))

        // === Lista por días como en tu canvas ===
        days.forEach { (dow, labelDia) ->
            val items = all.filter { it.dayOfWeek == dowToInt(dow) }
            if (items.isNotEmpty()) {
                DayBlock(labelDia, items, card, title)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Regresar", color = Color.DarkGray) }
    }

    if (showAdd) AddMealDialog(onDismiss = { showAdd = false }) { dayInt, mealType, text ->
        vm.setMeal(dayInt, mealType, text); showAdd = false
    }

    if (showDelete) DeleteMealDialog(onDismiss = { showDelete = false }) { dayInt, mealType ->
        vm.deleteMeal(dayInt, mealType); showDelete = false
    }
}

@Composable
private fun DayBlock(dia: String, items: List<com.example.healing.model.MealEntry>, card: Color, title: Color) {
    Surface(color = Color(0xFF92BEAB), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(dia, color = title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            val map = items.associateBy { it.mealType }
            listOf("Desayuno","Almuerzo","Snack","Cena").forEach { kind ->
                val desc = map[kind]?.description
                if (desc != null) {
                    Surface(color = card, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$kind", color = title, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(10.dp))
                            Text(desc, color = title)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun AddMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (dayOfWeek: Int, mealType: String, text: String) -> Unit
) {
    var dayIndex by remember { mutableStateOf(0) }  // 0..6
    var mealIndex by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar comida") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Día
                ExposedDropdownMenuBoxSample(
                    label = "Día",
                    items = days.map { it.second },
                    selectedIndex = dayIndex,
                    onSelected = { dayIndex = it }
                )
                // Tipo
                ExposedDropdownMenuBoxSample(
                    label = "Tipo",
                    items = meals,
                    selectedIndex = mealIndex,
                    onSelected = { mealIndex = it }
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Descripción") },
                    singleLine = false,
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dayInt = com.example.healing.viewmodel.MealPlanViewModel.dowToInt(
                    com.example.healing.viewmodel.MealPlanViewModel.days[dayIndex].first
                )
                onConfirm(dayInt, meals[mealIndex], text)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DeleteMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (dayOfWeek: Int, mealType: String) -> Unit
) {
    var dayIndex by remember { mutableStateOf(0) }
    var mealIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar comida") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBoxSample(
                    label = "Día",
                    items = days.map { it.second },
                    selectedIndex = dayIndex,
                    onSelected = { dayIndex = it }
                )
                ExposedDropdownMenuBoxSample(
                    label = "Tipo",
                    items = meals,
                    selectedIndex = mealIndex,
                    onSelected = { mealIndex = it }
                )
                Text("Se eliminará la comida seleccionada de ese día.")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dayInt = dowToInt(days[dayIndex].first)
                onConfirm(dayInt, meals[mealIndex])
            }) { Text("Eliminar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxSample(
    label: String,
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = items[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEachIndexed { i, text ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = { onSelected(i); expanded = false }
                )
            }
        }
    }
}
