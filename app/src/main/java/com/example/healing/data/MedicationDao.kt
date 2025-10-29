package com.example.healing.data

import androidx.room.*
import com.example.healing.model.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert suspend fun insert(m: Medication): Long

    @Update suspend fun update(m: Medication)

    @Delete suspend fun delete(m: Medication)

    @Query("SELECT * FROM medications WHERE dateEpochDay BETWEEN :start AND :end ORDER BY dateEpochDay, hour, minute")
    fun observeByRange(start: Long, end: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE dateEpochDay = :epochDay ORDER BY hour, minute")
    fun observeByDay(epochDay: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getById(id: Long): Medication?

    @Query("UPDATE medications SET taken = :taken WHERE id = :id")
    suspend fun setTaken(id: Long, taken: Boolean)
}
