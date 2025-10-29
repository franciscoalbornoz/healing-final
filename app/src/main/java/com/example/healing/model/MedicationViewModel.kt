package com.example.healing.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healing.data.MedicationDao
import com.example.healing.model.Medication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
// 👇 agregado: usamos el programador con WorkManager
import com.example.healing.notifications.ReminderScheduler

class MedicationViewModel(
    private val dao: MedicationDao,
    private val appContext: Context
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedDay: StateFlow<Long> = _selectedDay


    val medsOfDay = _selectedDay.flatMapLatest { day ->
        dao.observeByDay(day)
    }

    fun selectDay(epochDay: Long) { _selectedDay.value = epochDay }

    fun add(name: String, dose: Int, day: Long, hour: Int, minute: Int) = viewModelScope.launch {
        val id = dao.insert(
            Medication(
                name = name,
                doseCount = dose,
                dateEpochDay = day,
                hour = hour,
                minute = minute
            )
        )


        ReminderScheduler.scheduleWithWork(
            context = appContext,
            epochDay = day,
            hour = hour,
            minute = minute,
            title = name,
            dose = dose
        )

    }

    fun update(m: Medication) = viewModelScope.launch { dao.update(m) }

    fun delete(m: Medication) = viewModelScope.launch { dao.delete(m) }

    fun markTaken(id: Long, taken: Boolean) = viewModelScope.launch { dao.setTaken(id, taken) }

    fun medsBetween(startEpochDay: Long, endEpochDay: Long) =
        dao.observeByRange(startEpochDay, endEpochDay)
}

@Suppress("UNCHECKED_CAST")
class MedicationViewModelFactory(
    private val dao: MedicationDao,
    private val appContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            return MedicationViewModel(dao, appContext.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
