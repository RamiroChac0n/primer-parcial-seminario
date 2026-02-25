package com.example.primerparcial.ui.screens.habits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerparcial.data.AppDatabase
import com.example.primerparcial.data.entity.HabitCompletionEntity
import com.example.primerparcial.data.entity.HabitEntity
import com.example.primerparcial.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HabitWithStreak(
    val habit: HabitEntity,
    val streak: Int,
    val completedToday: Boolean
)

class HabitsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitRepository(AppDatabase.getDatabase(application).habitDao())

    val habitsWithStreak: StateFlow<List<HabitWithStreak>> = combine(
        repository.getAllHabits(),
        repository.getAllCompletions()
    ) { habits, completions ->
        val today = todayString()
        habits.map { habit ->
            val habitCompletions = completions.filter { it.habitId == habit.id }
            val streak = calculateStreak(habitCompletions)
            val completedToday = habitCompletions.any { it.completedDate == today }
            HabitWithStreak(habit, streak, completedToday)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleCompletion(habitId: Long, currentlyCompleted: Boolean) {
        viewModelScope.launch {
            val today = todayString()
            if (currentlyCompleted) {
                val completion = repository.getCompletion(habitId, today)
                completion?.let { repository.deleteCompletion(it) }
            } else {
                repository.insertCompletion(
                    HabitCompletionEntity(habitId = habitId, completedDate = today)
                )
            }
        }
    }

    fun addHabit(name: String, description: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(name = name, description = description, colorHex = colorHex)
            )
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun calculateStreak(completions: List<HabitCompletionEntity>): Int {
        if (completions.isEmpty()) return 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var streak = 0
        val sortedDates = completions.map { it.completedDate }.sortedDescending()
        for (dateStr in sortedDates) {
            val expected = dateFormat.format(calendar.time)
            if (dateStr == expected) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
