package com.example.healing.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "meals",
    indices = [Index(value = ["dayOfWeek","mealType"], unique = true)]
)
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int,         // 1..7  (Lunes=1 ... Domingo=7)
    val mealType: String,       // "Desayuno" | "Almuerzo" | "Snack" | "Cena"
    val description: String
)