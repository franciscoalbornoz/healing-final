package com.example.healing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healing.data.NotesDao
import com.example.healing.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val dao: NotesDao) : ViewModel() {


    val notes: StateFlow<List<Note>> =
        dao.observeAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(content: String) {
        val text = content.trim()
        if (text.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(content = text)) }
    }

    fun delete(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }

    fun update(note: Note, newContent: String) {
        val text = newContent.trim()
        if (text.isEmpty() || text == note.content) return
        viewModelScope.launch { dao.updateContent(note.id, text) }
    }
}

@Suppress("UNCHECKED_CAST")
class NotesViewModelFactory(private val dao: NotesDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
