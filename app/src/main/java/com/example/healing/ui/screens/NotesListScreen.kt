package com.example.healing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healing.model.Note
import com.example.healing.viewmodel.NotesViewModel

@Composable
fun NotesListScreen(navController: NavController, vm: NotesViewModel) {
    val notes by vm.notes.collectAsState()

    var openNote: Note? by remember { mutableStateOf(null) }
    var editNote: Note? by remember { mutableStateOf(null) }   // 👈 para editar
    var editText by remember { mutableStateOf("") }

    val bg = Color(0xFFCA9BFF)
    val card = Color(0xFFD1D0FB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Text("Notas guardadas", color = Color(0xFF2E235E), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(notes, key = { it.id }) { note ->
                NoteItemCard(
                    note = note,
                    cardColor = card,
                    onOpen = { openNote = note },
                    onDelete = { vm.delete(note) },
                    onEdit = {
                        editNote = note
                        editText = note.content
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { navController.popBackStack() }) { Text("Regresar", color = Color.DarkGray) }
    }


    if (openNote != null) {
        AlertDialog(
            onDismissRequest = { openNote = null },
            confirmButton = {
                TextButton(onClick = { openNote = null }) { Text("Cerrar", color = Color(0xFF2E235E)) }
            },
            title = { Text("Nota", color = Color(0xFF2E235E)) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFCA9BFF), shape = RoundedCornerShape(16.dp)) // 👈 fondo verde menta
                        .padding(16.dp)
                ) {
                    Text(
                        text = openNote!!.content,
                        color = Color(0xFF2E235E),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            containerColor = Color(0xFFB580FA) // 👈 también para todo el cuadro
        )
    }



    if (editNote != null) {
        AlertDialog(
            onDismissRequest = { editNote = null },
            title = { Text("Editar nota") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.update(editNote!!, editText)
                    editNote = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { editNote = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun NoteItemCard(
    note: Note,
    cardColor: Color,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        color = cardColor,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = note.content,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                color = Color(0xFF2E235E)
            )
            Spacer(Modifier.width(6.dp))
            TextButton(onClick = onEdit) { Text("Editar") }   //
            TextButton(onClick = onDelete) { Text("✕", color = Color.Black) }
        }
    }
}
