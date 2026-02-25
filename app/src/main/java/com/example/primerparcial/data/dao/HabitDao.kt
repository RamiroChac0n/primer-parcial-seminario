package com.example.primerparcial.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.primerparcial.data.entity.HabitCompletionEntity
import com.example.primerparcial.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habit_completions ORDER BY completedDate DESC")
    fun getAllCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedDate DESC")
    suspend fun getAllCompletionsForHabit(habitId: Long): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completedDate = :date LIMIT 1")
    suspend fun getCompletion(habitId: Long, date: String): HabitCompletionEntity?

    @Insert
    suspend fun insertCompletion(completion: HabitCompletionEntity)

    @Delete
    suspend fun deleteCompletion(completion: HabitCompletionEntity)
}
