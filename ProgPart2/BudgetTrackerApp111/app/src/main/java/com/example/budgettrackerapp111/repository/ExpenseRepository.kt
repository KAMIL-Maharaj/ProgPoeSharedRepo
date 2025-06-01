package com.example.budgettrackerapp111.repository

import androidx.lifecycle.LiveData
import com.example.budgettrackerapp111.data.Expense
import com.example.budgettrackerapp111.data.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    // All functions directly use the expenseDao passed in the constructor

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun getCategorySpending(categoryName: String, startDate: String, endDate: String): LiveData<Double> {
        return expenseDao.getCategorySpending(categoryName, startDate, endDate)
    }
    fun getExpensesByCategory(categoryName: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(categoryName)
    }
    fun getExpensesBetween(start: String, end: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesBetween(start, end)
    }

    fun getExpensesByCategoryAndDate(category: String, startDate: String, endDate: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategoryAndDate(category, startDate, endDate)
    }

    fun getTotalSpendingForCategory(category: String, startDate: String, endDate: String): LiveData<Double> {
        return expenseDao.getTotalSpendingForCategory(category, startDate, endDate)
    }

}