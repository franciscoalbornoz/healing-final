package com.example.healing.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.healing.model.Medication

@Database(entities = [Medication::class], version = 1, exportSchema = false)
abstract class MedsDatabase : RoomDatabase() {
    abstract fun dao(): MedicationDao

    companion object {
        @Volatile private var INSTANCE: MedsDatabase? = null
        fun get(context: Context): MedsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MedsDatabase::class.java,
                    "meds.db"
                ).build().also { INSTANCE = it }
            }
    }
}
