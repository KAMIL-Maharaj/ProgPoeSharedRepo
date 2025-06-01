package com.example.budgettrackerapp111.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.budgettrackerapp111.data.Expense
import com.example.budgettrackerapp111.repository.ExpenseRepository

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Fetch expenses within a date range
    fun getExpensesBetween(startDate: String, endDate: String): LiveData<List<Expense>> {
        return repository.getExpensesBetween(startDate, endDate)
    }

    // Fetch spending for a specific category within a date range
    fun getCategorySpending(categoryName: String, startDate: String, endDate: String): LiveData<Double> {
        return repository.getCategorySpending(categoryName, startDate, endDate)
    }

    fun getExpensesByCategoryAndDate(category: String, startDate: String, endDate: String): LiveData<List<Expense>> {
        return repository.getExpensesByCategoryAndDate(category, startDate, endDate)
    }

    fun getExpensesByCategory(categoryName: String): LiveData<List<Expense>> {
        return repository.getExpensesByCategory(categoryName)
    }
}
