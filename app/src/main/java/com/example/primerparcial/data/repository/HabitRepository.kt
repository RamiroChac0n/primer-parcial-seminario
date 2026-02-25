package com.example.primerparcial.data.repository

import com.example.primerparcial.data.dao.HabitDao
import com.example.primerparcial.data.entity.HabitCompletionEntity
import com.example.primerparcial.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()

    fun getAllCompletions(): Flow<List<HabitCompletionEntity>> = habitDao.getAllCompletions()

    suspend fun getAllCompletionsForHabit(habitId: Long): List<HabitCompletionEntity> =
        habitDao.getAllCompletionsForHabit(habitId)

    suspend fun getCompletion(habitId: Long, date: String): HabitCompletionEntity? =
        habitDao.getCompletion(habitId, date)

    suspend fun insertHabit(habit: HabitEntity): Long = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    suspend fun insertCompletion(completion: HabitCompletionEntity) =
        habitDao.insertCompletion(completion)

    suspend fun deleteCompletion(completion: HabitCompletionEntity) =
        habitDao.deleteCompletion(completion)
}
