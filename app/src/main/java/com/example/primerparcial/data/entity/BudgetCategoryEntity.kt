package com.example.primerparcial.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_categories")
data class BudgetCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val monthlyLimit: Double = 0.0,
    val month: Int,
    val year: Int
)
