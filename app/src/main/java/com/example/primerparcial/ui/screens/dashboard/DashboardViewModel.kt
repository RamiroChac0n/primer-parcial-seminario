package com.example.primerparcial.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerparcial.data.AppDatabase
import com.example.primerparcial.data.entity.HabitCompletionEntity
import com.example.primerparcial.data.repository.BudgetRepository
import com.example.primerparcial.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DashboardState(
    val totalHabits: Int = 0,
    val completedToday: Int = 0,
    val topStreak: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val weeklyCompletions: List<Int> = List(7) { 0 }
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val habitRepo = HabitRepository(db.habitDao())
    private val budgetRepo = BudgetRepository(db.budgetDao())

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)
    private val monthPrefix = String.format("%04d-%02d", currentYear, currentMonth)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    val dashboardState: StateFlow<DashboardState> = combine(
        habitRepo.getAllHabits(),
        habitRepo.getAllCompletions(),
        budgetRepo.getTransactionsForMonth(monthPrefix)
    ) { habits, completions, transactions ->

        val completedTodayCount = completions.count { it.completedDate == today }

        val topStreak = habits.maxOfOrNull { habit ->
            calculateStreak(completions.filter { it.habitId == habit.id })
        } ?: 0

        val weeklyData = (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dateStr = dateFormat.format(cal.time)
            completions.count { it.completedDate == dateStr }
        }

        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }

        DashboardState(
            totalHabits = habits.size,
            completedToday = completedTodayCount,
            topStreak = topStreak,
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses,
            weeklyCompletions = weeklyData
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    private fun calculateStreak(completions: List<HabitCompletionEntity>): Int {
        if (completions.isEmpty()) return 0
        val cal = Calendar.getInstance()
        var streak = 0
        val sortedDates = completions.map { it.completedDate }.sortedDescending()
        for (dateStr in sortedDates) {
            val expected = dateFormat.format(cal.time)
            if (dateStr == expected) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
