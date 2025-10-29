// app/src/main/java/com/example/healing/viewmodel/MealPlanViewModel.kt
package com.example.healing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healing.data.MealDao
import com.example.healing.model.MealEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class MealPlanViewModel(private val dao: MealDao): ViewModel() {

    // Todo el plan (para pintar como en tu canvas por días)
    val allMeals: StateFlow<List<MealEntry>> =
        dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setMeal(dayOfWeek: Int, mealType: String, description: String) = viewModelScope.launch {
        val text = description.trim()
        if (text.isEmpty()) return@launch
        dao.upsert(MealEntry(dayOfWeek = dayOfWeek, mealType = mealType, description = text))
    }

    fun deleteMeal(dayOfWeek: Int, mealType: String) = viewModelScope.launch {
        dao.deleteByKey(dayOfWeek, mealType)
    }

    companion object {
        val days = listOf(
            DayOfWeek.MONDAY to "Lunes",
            DayOfWeek.TUESDAY to "Martes",
            DayOfWeek.WEDNESDAY to "Miércoles",
            DayOfWeek.THURSDAY to "Jueves",
            DayOfWeek.FRIDAY to "Viernes",
            DayOfWeek.SATURDAY to "Sábado",
            DayOfWeek.SUNDAY to "Domingo",
        )
        val meals = listOf("Desayuno","Almuerzo","Snack","Cena")
        fun dowToInt(d: DayOfWeek) = d.value // Lunes=1
    }
}

@Suppress("UNCHECKED_CAST")
class MealPlanViewModelFactory(private val dao: MealDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlanViewModel::class.java)) {
            return MealPlanViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
