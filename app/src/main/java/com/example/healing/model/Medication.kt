package com.example.healing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val doseCount: Int,
    val dateEpochDay: Long,
    val hour: Int,
    val minute: Int,
    val taken: Boolean = false
)
