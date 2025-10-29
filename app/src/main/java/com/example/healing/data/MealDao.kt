
package com.example.healing.data

import androidx.room.*
import com.example.healing.model.MealEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meals WHERE dayOfWeek = :day ORDER BY mealType")
    fun observeByDay(day: Int): Flow<List<MealEntry>>

    @Query("SELECT * FROM meals ORDER BY dayOfWeek, mealType")
    fun observeAll(): Flow<List<MealEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealEntry)

    @Delete
    suspend fun delete(meal: MealEntry)

    @Query("DELETE FROM meals WHERE dayOfWeek = :day AND mealType = :mealType")
    suspend fun deleteByKey(day: Int, mealType: String)
}
