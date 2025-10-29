package com.example.healing.notifications

import android.content.Context
import androidx.work.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleWithWork(
        context: Context,
        epochDay: Long,
        hour: Int,
        minute: Int,
        title: String,
        dose: Int
    ) {

        val triggerMillis = LocalDate.ofEpochDay(epochDay)
            .atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val now = System.currentTimeMillis()
        val delay = (triggerMillis - now).coerceAtLeast(0L)

        val data = workDataOf(
            "title" to title,
            "dose"  to dose
        )

        val request = OneTimeWorkRequestBuilder<MedicationWork>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
