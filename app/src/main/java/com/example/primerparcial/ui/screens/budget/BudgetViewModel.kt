package com.example.primerparcial.ui.screens.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerparcial.data.AppDatabase
import com.example.primerparcial.data.entity.BudgetCategoryEntity
import com.example.primerparcial.data.entity.TransactionEntity
import com.example.primerparcial.data.repository.BudgetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class BudgetSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0
)

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BudgetRepository(AppDatabase.getDatabase(application).budgetDao())

    private val calendar = Calendar.getInstance()
    val currentMonth: Int = calendar.get(Calendar.MONTH) + 1
    val currentYear: Int = calendar.get(Calendar.YEAR)
    private val monthPrefix = String.format("%04d-%02d", currentYear, currentMonth)

    val transactions: StateFlow<List<TransactionEntity>> =
        repository.getTransactionsForMonth(monthPrefix)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<BudgetCategoryEntity>> =
        repository.getCategoriesForMonth(currentMonth, currentYear)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<BudgetSummary> = transactions.map { list ->
        val income = list.filter { it.type == "income" }.sumOf { it.amount }
        val expenses = list.filter { it.type == "expense" }.sumOf { it.amount }
        BudgetSummary(income, expenses, income - expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetSummary())

    fun addTransaction(amount: Double, type: String, description: String, categoryId: Long?) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    description = description,
                    categoryId = categoryId,
                    date = date
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addCategory(name: String, monthlyLimit: Double) {
        viewModelScope.launch {
            repository.insertCategory(
                BudgetCategoryEntity(
                    name = name,
                    monthlyLimit = monthlyLimit,
                    month = currentMonth,
                    year = currentYear
                )
            )
        }
    }
}
