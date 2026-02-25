package com.example.primerparcial.data.repository

import com.example.primerparcial.data.dao.BudgetDao
import com.example.primerparcial.data.entity.BudgetCategoryEntity
import com.example.primerparcial.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getCategoriesForMonth(month: Int, year: Int): Flow<List<BudgetCategoryEntity>> =
        budgetDao.getCategoriesForMonth(month, year)

    fun getAllCategories(): Flow<List<BudgetCategoryEntity>> = budgetDao.getAllCategories()

    fun getTransactionsForMonth(monthPrefix: String): Flow<List<TransactionEntity>> =
        budgetDao.getTransactionsForMonth(monthPrefix)

    suspend fun insertCategory(category: BudgetCategoryEntity): Long =
        budgetDao.insertCategory(category)

    suspend fun updateCategory(category: BudgetCategoryEntity) =
        budgetDao.updateCategory(category)

    suspend fun deleteCategory(category: BudgetCategoryEntity) =
        budgetDao.deleteCategory(category)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        budgetDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        budgetDao.deleteTransaction(transaction)
}
