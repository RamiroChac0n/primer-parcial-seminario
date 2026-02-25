package com.example.primerparcial.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.primerparcial.data.dao.BudgetDao
import com.example.primerparcial.data.dao.HabitDao
import com.example.primerparcial.data.entity.BudgetCategoryEntity
import com.example.primerparcial.data.entity.HabitCompletionEntity
import com.example.primerparcial.data.entity.HabitEntity
import com.example.primerparcial.data.entity.TransactionEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        BudgetCategoryEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "primerparcial_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
