package com.example.budgettrackerapp111.repository

import androidx.lifecycle.LiveData
import com.example.budgettrackerapp111.data.Category
import com.example.budgettrackerapp111.data.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }
    fun getCategoryByName(name: String): LiveData<Category> {
        return categoryDao.getCategoryByName(name)
    }
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }
    suspend fun updateGoals(categoryId: Int, minGoal: Double, maxGoal: Double) {
        categoryDao.updateGoals(categoryId, minGoal, maxGoal)
    }
    suspend fun getCategoryById(categoryId: Int): Category? {
        return categoryDao.getCategoryById(categoryId)
    }

}
