package com.example.budgettrackerapp111.viewmodel

import androidx.lifecycle.*
import com.example.budgettrackerapp111.data.Category
import com.example.budgettrackerapp111.repository.CategoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    // LiveData to observe all categories
    val allCategories: LiveData<List<Category>> = repository.getAllCategories().asLiveData()

    // Insert or update a category
    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    // Delete a category
    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCategory(category)
    }
    fun getCategoryByName(name: String): LiveData<Category> {
        return repository.getCategoryByName(name)
    }
    // Update goals for a specific category
    fun updateGoals(categoryId: Int, minGoal: Double, maxGoal: Double) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateGoals(categoryId, minGoal, maxGoal)
    }

    // Get a specific category by ID
    fun getCategoryById(categoryId: Int): LiveData<Category?> {
        val categoryLiveData = MutableLiveData<Category?>()
        viewModelScope.launch(Dispatchers.IO) {
            val category = repository.getCategoryById(categoryId) // suspend function call inside a coroutine
            categoryLiveData.postValue(category) // Set the result on LiveData
        }
        return categoryLiveData
    }
}


class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}