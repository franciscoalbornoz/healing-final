
package com.example.healing.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.healing.model.MealEntry

@Database(entities = [MealEntry::class], version = 1, exportSchema = false)
abstract class MealsDatabase : RoomDatabase() {
    abstract fun dao(): MealDao

    companion object {
        @Volatile private var INSTANCE: MealsDatabase? = null
        fun get(context: Context): MealsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MealsDatabase::class.java,
                    "meals.db"
                ).build().also { INSTANCE = it }
            }
    }
}
