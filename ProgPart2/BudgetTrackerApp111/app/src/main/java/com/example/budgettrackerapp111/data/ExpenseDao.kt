package com.example.budgettrackerapp111.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense_table")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expense_table WHERE date BETWEEN :start AND :end")
    fun getExpensesBetween(start: String, end: String): LiveData<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense_table WHERE category = :categoryName AND date BETWEEN :startDate AND :endDate")
    fun getCategorySpending(
        categoryName: String, startDate: String, endDate: String): LiveData<Double>

    @Query("""
    SELECT SUM(amount) FROM expense_table
    WHERE category = :category
    AND date BETWEEN :startDate AND :endDate
""")
    fun getTotalSpendingForCategory(category: String, startDate: String, endDate: String): LiveData<Double>


    @Query("SELECT * FROM expense_table WHERE category = :category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByCategoryAndDate(category: String, startDate: String, endDate: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expense_table WHERE category = :categoryName ORDER BY date DESC")
    fun getExpensesByCategory(categoryName: String): LiveData<List<Expense>>
}










