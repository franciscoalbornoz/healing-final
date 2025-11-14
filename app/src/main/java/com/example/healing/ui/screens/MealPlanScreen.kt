package com.example.healing.ui.screens

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.healing.data.MealImageStore
import com.example.healing.model.MealEntry
import com.example.healing.viewmodel.MealPlanViewModel
import com.example.healing.viewmodel.MealPlanViewModel.Companion.days
import com.example.healing.viewmodel.MealPlanViewModel.Companion.dowToInt
import com.example.healing.viewmodel.MealPlanViewModel.Companion.meals

@Composable
fun MealPlanScreen(navController: NavController, vm: MealPlanViewModel) {
    val bg = Color(0xFFA8D5BA)
    val card = Color(0xFFD1D0FB)
    val title = Color(0xFF2E235E)

    val all by vm.allMeals.collectAsState()
    val context = LocalContext.current
    val imageStore = remember { MealImageStore(context) }

    var showAdd by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Text(
            "Plan Alimenticio",
            color = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
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

        // Mostrar por días
        days.forEach { (dow, labelDia) ->
            val dayInt = dowToInt(dow)
            val items = all.filter { it.dayOfWeek == dayInt }
            if (items.isNotEmpty()) {
                DayBlock(
                    dia = labelDia,
                    dayInt = dayInt,
                    items = items,
                    card = card,
                    title = title,
                    imageStore = imageStore
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Regresar", color = Color.DarkGray)
        }
    }

    if (showAdd) {
        AddMealDialog(
            onDismiss = { showAdd = false }
        ) { dayInt, mealType, text, imageUri ->
            vm.setMeal(dayInt, mealType, text)

            // Guardar imagen si hay
            imageUri?.toString()?.let { uriStr ->
                imageStore.saveImage(dayInt, mealType, uriStr)
            }

            showAdd = false
        }
    }

    if (showDelete) {
        DeleteMealDialog(onDismiss = { showDelete = false }) { dayInt, mealType ->
            vm.deleteMeal(dayInt, mealType)
            showDelete = false
        }
    }
}

@Composable
private fun DayBlock(
    dia: String,
    dayInt: Int,
    items: List<MealEntry>,
    card: Color,
    title: Color,
    imageStore: MealImageStore
) {
    Surface(
        color = Color(0xFF92BEAB),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(dia, color = title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))

            val map = items.associateBy { it.mealType }

            listOf("Desayuno", "Almuerzo", "Snack", "Cena").forEach { kind ->
                val entry = map[kind]
                val desc = entry?.description

                if (desc != null) {
                    val imageUriStr = imageStore.getImage(dayInt, kind)
                    val hasImage = imageUriStr != null

                    Surface(
                        color = card,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(kind, color = title, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(10.dp))
                                Text(desc, color = title)
                            }

                            if (hasImage) {
                                Spacer(Modifier.height(6.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(imageUriStr),
                                    contentDescription = "Foto $kind",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
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
    onConfirm: (dayOfWeek: Int, mealType: String, text: String, imageUri: Uri?) -> Unit
) {
    val context = LocalContext.current

    var dayIndex by remember { mutableStateOf(0) }  // 0..6
    var mealIndex by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempCameraUri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar comida") },
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
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Descripción") },
                    singleLine = false,
                    minLines = 2
                )

                // Preview de imagen
                selectedImageUri?.let { uri ->
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Galería") }

                    OutlinedButton(
                        onClick = {
                            val uri = createImageUri(context)
                            tempCameraUri = uri
                            if (uri != null) {
                                cameraLauncher.launch(uri)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Cámara") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dayInt = dowToInt(days[dayIndex].first)
                onConfirm(dayInt, meals[mealIndex], text, selectedImageUri)
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

// Helper para crear Uri para la cámara
private fun createImageUri(context: Context): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "meal_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}
