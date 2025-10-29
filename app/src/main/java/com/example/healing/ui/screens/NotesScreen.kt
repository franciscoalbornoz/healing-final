package com.example.healing.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.healing.R
import com.example.healing.viewmodel.NotesViewModel
import com.example.healing.navigation.Route   // ← agregado

@Composable
fun NotesScreen(
    navController: NavController,
    vm: NotesViewModel
) {
    var noteText by remember { mutableStateOf("") }

    // Paleta usada en tu diseño
    val backgroundColor = Color(0xFFA8D5BA)
    val textBoxColor    = Color(0xFFD1D0FB)
    val titleTextColor  = Color(0xFF2E235E)
    val buttonColor     = Color(0xFF63918B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Mi Agenda", fontSize = 16.sp, color = Color.DarkGray)
            Text(
                text = "14:20 p. m.\nJueves 25 de sept",
                fontSize = 12.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(16.dp))


        Text(
            text = "Bienvenido a tu\nblock de notas",
            color = titleTextColor,
            fontSize = 28.sp,              // más grande
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(textBoxColor)
                .padding(20.dp)
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.ic_lapiz),
                    contentDescription = "Lápiz",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                BasicTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),    // ¡más alto!
                    textStyle = LocalTextStyle.current.copy(
                        color = Color(0xFF2E235E),
                        fontSize = 18.sp    // texto más grande
                    ),
                    decorationBox = { innerTextField ->
                        if (noteText.isEmpty()) {
                            Text(
                                text = "Escribe tu nota...",
                                color = Color(0xFF6E6E6E),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                vm.add(noteText)
                noteText = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) { Text("Guardar texto", color = Color.Black) }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate(Route.NotesList.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) { Text("Ver blocks guardados", color = Color.Black) }

        Spacer(Modifier.height(20.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Regresar", color = Color.DarkGray)
        }
    }
}
