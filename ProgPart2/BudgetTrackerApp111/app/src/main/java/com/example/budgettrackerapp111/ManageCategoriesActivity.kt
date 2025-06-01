package com.example.budgettrackerapp111

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budgettrackerapp111.data.AppDatabase
import com.example.budgettrackerapp111.data.Category
import com.example.budgettrackerapp111.repository.CategoryRepository
import com.example.budgettrackerapp111.viewmodel.CategoryViewModel
import com.example.budgettrackerapp111.viewmodel.CategoryViewModelFactory

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: ArrayAdapter<String>
    private var categoryList: List<Category> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_categories)

        val dao = AppDatabase.getDatabase(applicationContext).categoryDao()
        val repository = CategoryRepository(dao)
        val factory = CategoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        val input = findViewById<EditText>(R.id.category_name_input)
        val addButton = findViewById<Button>(R.id.add_category_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val listView = findViewById<ListView>(R.id.category_list)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listView.adapter = adapter

        addButton.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                val category = Category(name = name)
                viewModel.insertCategory(category)
                Toast.makeText(this, "Category successfully created", Toast.LENGTH_SHORT).show()
                input.text.clear()
            }
        }

        // Go back to Dashboard
        backButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        viewModel.allCategories.observe(this) { categories ->
            categoryList = categories
            adapter.clear()
            adapter.addAll(categories.map { it.name })
        }

        // Delete on long click
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]
            viewModel.deleteCategory(selectedCategory)
            Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
            true
        }

        // Edit on click
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]
            input.setText(selectedCategory.name)
            addButton.text = "Update Category"

            addButton.setOnClickListener {
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedCategory = Category(id = selectedCategory.id, name = newName)
                    viewModel.insertCategory(updatedCategory) // REPLACE strategy handles update
                    Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                    input.text.clear()
                    addButton.text = "Add Category"
                }
            }
        }
    }
}
