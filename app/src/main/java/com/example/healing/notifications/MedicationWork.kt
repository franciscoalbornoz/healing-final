package com.example.healing.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MedicationWork(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Medicamento"
        val dose  = inputData.getInt("dose", 1)
        MedicationNotifier.show(ctx, title, dose)
        return Result.success()
    }
}
