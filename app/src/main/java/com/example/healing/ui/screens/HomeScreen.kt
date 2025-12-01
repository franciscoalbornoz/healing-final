package com.example.healing.ui.screens

import com.example.healing.navigation.Route
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healing.R
import com.example.healing.viewmodel.HomeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.healing.data.Prefs
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel = viewModel()
) {
    val bg = Color(0xFFA17CEE)          // fondo lila
    val headline = Color(0xFF2F143D)    // título "Mi Agenda" / "Healing"
    val chip = Color(0xFFC99AFD)        // chips normal (Herramientas)

    // 👇 CAMBIO SOLICITADO: Color específico para el círculo del Tracker
    val habitCircleColor = Color(0xFF626698)

    val chipSelected = Color(0xFF626698) // chip seleccionado (mismo tono)
    val barTrack = Color(0xFFB38BFF)    // barra de progreso (track)
    val barFill = Color(0xFF8A5BFF)     // barra llena

    // Prefs para persistencia
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        prefs.selectedHabitsFlow.collect { saved ->
            vm.setSelectedFromStorage(saved)
        }
    }

    // Hora/fecha
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(60_000L)
        }
    }
    val locale = Locale.getDefault()
    val timeText = remember(now) { now.format(DateTimeFormatter.ofPattern("h:mm a", locale)) }
    val dateText = remember(now) { now.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMM", locale)) }

    val selectedState by vm.selected.collectAsState()
    val targetProgress = selectedState.size / vm.habits.size.toFloat()
    val progress by animateFloatAsState(targetValue = targetProgress, label = "progressAnim")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {

        val screenW = maxWidth
        val baseWidth = 360.dp
        val scale = (screenW / baseWidth).coerceIn(0.7f, 1.15f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (20.dp * scale))
                .padding(top = (18.dp * scale), bottom = (28.dp * scale))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mi Agenda",
                    fontSize = (maxOf(28f * scale, 16f)).sp,
                    color = headline,
                    fontWeight = FontWeight.Bold
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = timeText,
                        color = headline,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dateText.replaceFirstChar { it.uppercase() },
                        color = headline,
                        fontSize = (maxOf(13f * scale, 10f)).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height((22.dp * scale)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del día",
                    color = headline,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = headline,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((34.dp * scale))
                    .clip(RoundedCornerShape((18.dp * scale)))
                    .background(barTrack.copy(alpha = 0.9f))
                    .padding(horizontal = (12.dp * scale), vertical = (8.dp * scale))
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((18.dp * scale))
                        .clip(RoundedCornerShape((12.dp * scale))),
                    color = barFill,
                    trackColor = Color.Transparent
                )
            }

            Spacer(modifier = Modifier.height((28.dp * scale)))

            Text(
                text = "Tracker de hábitos",
                color = headline,
                fontWeight = FontWeight.SemiBold,
                fontSize = (maxOf(18f * scale, 12f)).sp
            )
            Spacer(modifier = Modifier.height((14.dp * scale)))

            HabitGrid(
                ids = vm.habits.map { it.id },
                labels = vm.habits.map { it.label },
                icons = listOf(
                    R.drawable.ic_agua,
                    R.drawable.ic_trotar,
                    R.drawable.ic_leer,
                    R.drawable.ic_comer_sano,
                    R.drawable.ic_ejercicios,
                    R.drawable.ic_pasear_mascota
                ),
                selected = selectedState,
                // 👇 AQUÍ USAMOS EL NUEVO COLOR SOLO PARA LOS HÁBITOS
                chip = habitCircleColor,
                chipSelected = chipSelected,
                onToggle = { id ->
                    vm.toggleHabit(id)
                    scope.launch { prefs.setSelectedHabits(vm.selected.value) }
                },
                scale = scale
            )

            Spacer(modifier = Modifier.height((30.dp * scale)))

            Text(
                text = "Herramientas",
                color = headline,
                fontWeight = FontWeight.SemiBold,
                fontSize = (maxOf(20f * scale, 14f)).sp
            )
            Spacer(modifier = Modifier.height((16.dp * scale)))

            ToolsGrid(
                navController = navController,
                // 👇 LAS HERRAMIENTAS SIGUEN USANDO EL COLOR CLARO ORIGINAL
                chip = chip,
                scale = scale,
                onClick = { /* sin acción por ahora */ }
            )
        }
    }
}

@Composable
private fun HabitGrid(
    ids: List<String>,
    labels: List<String>,
    icons: List<Int>,
    selected: Set<String>,
    chip: Color,
    chipSelected: Color,
    onToggle: (String) -> Unit,
    scale: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((26.dp * scale), Alignment.CenterHorizontally)
        ) {
            for (i in 0..2)
                HabitItem(ids[i], labels[i], icons[i], selected, chip, chipSelected, onToggle, scale)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((26.dp * scale), Alignment.CenterHorizontally)
        ) {
            for (i in 3..5)
                HabitItem(ids[i], labels[i], icons[i], selected, chip, chipSelected, onToggle, scale)
        }
    }
}

@Composable
private fun HabitItem(
    id: String,
    label: String,
    iconRes: Int,
    selected: Set<String>,
    chip: Color,
    chipSelected: Color,
    onToggle: (String) -> Unit,
    scale: Float
) {
    val scaleF = scale.coerceAtLeast(0.7f)
    val isSelected = id in selected

    val pulse = rememberInfiniteTransition(label = "glowPulse").animateFloat(
        initialValue = 1.0f,
        targetValue  = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size((80.dp * scaleF))
                .drawBehind {
                    if (isSelected) {
                        val rBase = size.minDimension / 2f
                        val r = rBase * 0.9f * pulse.value
                        drawCircle(
                            color = Color.White.copy(alpha = 0.45f),
                            radius = r
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.20f),
                            radius = r * 1.20f
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((78.dp * scaleF))
                    .clip(CircleShape)
                    .background(chip)
                    .clickable { onToggle(id) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size((40.dp * scaleF))
                )
            }
        }

        Spacer(modifier = Modifier.height((8.dp * scaleF)))
        Text(
            text = label,
            color = Color(0xFF2E235E),
            textAlign = TextAlign.Center,
            fontSize = (maxOf(12f * scaleF, 10f)).sp
        )
    }
}

@Composable
private fun ToolsGrid(
    navController: NavController,
    chip: Color,
    scale: Float,
    onClick: () -> Unit
) {
    val items = listOf(
        "Notas", "Dieta",
        "Contacto de\nemergencia", "Datos\npersonales",
        "Horario\nmedicamentos", "Plan de\nalimentación"
    )
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((18.dp * scale), Alignment.CenterHorizontally)
        ) {
            ToolButton(items[0], chip, scale = scale, onClick = { navController.navigate(Route.Notes.route) })
            ToolButton(items[1], chip, scale = scale, onClick = { navController.navigate(Route.FoodPlan.route) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((18.dp * scale), Alignment.CenterHorizontally)
        ) {

            ToolButton(items[2], chip, scale = scale, onClick = { navController.navigate(Route.EmergencyContact.route) })
            ToolButton(items[3], chip, scale = scale, onClick = { navController.navigate(Route.Personal.route) })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((18.dp * scale), Alignment.CenterHorizontally)
        ) {
            ToolButton(items[4], chip, scale = scale, onClick = { navController.navigate(Route.MedsCalendar.route) })
        }
    }
}

@Composable
private fun ToolButton(text: String, chip: Color, scale: Float = 1f, onClick: () -> Unit) {
    val factor = scale
    Surface(
        color = chip,
        shape = RoundedCornerShape((24.dp * factor)),
        modifier = Modifier
            .width((160.dp * factor))
            .height((45.dp * factor))
            .clickable { onClick() }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = ((14f * factor).sp)
            )
        }
    }
}