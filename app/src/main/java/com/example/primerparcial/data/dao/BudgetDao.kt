package com.example.primerparcial.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.primerparcial.data.entity.BudgetCategoryEntity
import com.example.primerparcial.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budget_categories WHERE month = :month AND year = :year ORDER BY name ASC")
    fun getCategoriesForMonth(month: Int, year: Int): Flow<List<BudgetCategoryEntity>>

    @Query("SELECT * FROM budget_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<BudgetCategoryEntity>>

    @Insert
    suspend fun insertCategory(category: BudgetCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: BudgetCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: BudgetCategoryEntity)

    @Query("SELECT * FROM transactions WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getTransactionsForMonth(monthPrefix: String): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}
