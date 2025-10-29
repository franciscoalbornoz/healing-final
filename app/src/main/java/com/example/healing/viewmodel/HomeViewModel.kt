package com.example.healing.viewmodel

import androidx.lifecycle.ViewModel
import com.example.healing.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {


    val habits: List<Habit> = listOf(
        Habit("agua", "Agua"),
        Habit("trotar", "Trotar"),
        Habit("leer", "Leer"),
        Habit("comer_sano", "Comer Sano"),
        Habit("ejercicios", "Ejercicios"),
        Habit("pasear", "Pasear mascota")
    )

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected

    fun toggleHabit(id: String) {
        _selected.update { set ->
            if (id in set) set - id else set + id
        }
    }


    fun progress(): Float = _selected.value.size / habits.size.toFloat()
    fun percent(): Int = (progress() * 100).toInt()
    fun setSelectedFromStorage(ids: Set<String>) {
        _selected.value = ids
    }
}
