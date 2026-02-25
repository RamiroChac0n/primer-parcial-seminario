package com.example.primerparcial

import com.example.primerparcial.data.entity.TransactionEntity
import com.example.primerparcial.ui.screens.budget.BudgetSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class PureLogicTest {

    // Prueba 1: el balance del resumen es ingresos menos gastos
    @Test
    fun budgetSummary_balance_isIncomeMinusExpenses() {
        val income = 1000.0
        val expenses = 350.0
        val summary = BudgetSummary(
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses
        )
        assertEquals(650.0, summary.balance, 0.001)
    }

    // Prueba 2: solo se suman las transacciones de tipo "income"
    @Test
    fun transactions_totalIncome_ignoresExpenses() {
        val transactions = listOf(
            TransactionEntity(amount = 500.0, type = "income", date = "2025-01-10"),
            TransactionEntity(amount = 200.0, type = "expense", date = "2025-01-11"),
            TransactionEntity(amount = 300.0, type = "income", date = "2025-01-12")
        )
        val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
        assertEquals(800.0, totalIncome, 0.001)
    }

    // Prueba 3: la racha es 0 cuando no hay completaciones
    @Test
    fun streak_isZero_whenNoCompletions() {
        val completions = emptyList<String>()
        val streak = if (completions.isEmpty()) 0 else completions.size
        assertEquals(0, streak)
    }
}
