package com.example.budgettrackerapp111.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // Insert or replace a category with the same primary key
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    // Delete a category from the database
    @Delete
    suspend fun deleteCategory(category: Category)

    // Retrieve all categories ordered by name (ascending)
    @Query("SELECT * FROM category_table ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>


    @Query("SELECT * FROM category_table WHERE name = :name LIMIT 1")
    fun getCategoryByName(name: String): LiveData<Category>

    @Query("UPDATE category_table SET minGoal = :minGoal, maxGoal = :maxGoal WHERE id = :categoryId")
    suspend fun updateGoals(categoryId: Int, minGoal: Double, maxGoal: Double)


    @Query("SELECT * FROM category_table WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?
    // OPTIONAL: Update an existing category
    // Uncomment if you want to allow updates explicitly
    /*

    @Update
    suspend fun updateCategory(category: Category)
    */
}
