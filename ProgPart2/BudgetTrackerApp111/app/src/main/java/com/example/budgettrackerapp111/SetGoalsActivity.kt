package com.example.budgettrackerapp111

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.budgettrackerapp111.data.AppDatabase
import com.example.budgettrackerapp111.data.Category
import com.example.budgettrackerapp111.repository.ExpenseRepository
import com.example.budgettrackerapp111.repository.CategoryRepository
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModel
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModelFactory
import com.example.budgettrackerapp111.viewmodel.CategoryViewModel
import com.example.budgettrackerapp111.viewmodel.CategoryViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class SetGoalsActivity : AppCompatActivity() {
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var currentSpendingText: TextView
    private lateinit var spendingProgress: ProgressBar
    private var selectedCategory: Category? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goals)

        // Initialize DAOs and Repositories
        val database = AppDatabase.getDatabase(this)
        val expenseRepository = ExpenseRepository(database.expenseDao())
        val categoryRepository = CategoryRepository(database.categoryDao())

        // Initialize ViewModels
        expenseViewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(expenseRepository)
        )[ExpenseViewModel::class.java]

        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepository)
        )[CategoryViewModel::class.java]

        // Initialize views
        categorySpinner = findViewById(R.id.category_spinner)
        minGoalInput = findViewById(R.id.min_goal_input)
        maxGoalInput = findViewById(R.id.max_goal_input)
        currentSpendingText = findViewById(R.id.current_spending)
        spendingProgress = findViewById(R.id.spending_progress)

        // Load categories
        categoryViewModel.allCategories.observe(this) { categories ->
            if (categories.isNotEmpty()) {
                setupCategorySpinner(categories)
            }
        }

        // Save button
        findViewById<Button>(R.id.save_goal_button).setOnClickListener {
            saveGoals()
        }
    }

    private fun setupCategorySpinner(categories: List<Category>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectedCategory = categories[position]
                updateUIForSelectedCategory()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateUIForSelectedCategory() {
        val category = selectedCategory ?: return

        // Update input fields
        minGoalInput.setText(category.minGoal?.toString() ?: "")
        maxGoalInput.setText(category.maxGoal?.toString() ?: "")

        // Get this month's date range
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val startOfMonth = String.format("%04d-%02d-01", year, month)
        val endOfMonth = String.format(
            "%04d-%02d-%02d",
            year,
            month,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        // Observe spending
        expenseViewModel.getCategorySpending(category.name, startOfMonth, endOfMonth)
            .observe(this) { spending ->
                val safeSpending = spending ?: 0.0
                currentSpendingText.text = "$${"%.2f".format(safeSpending)}"
                updateProgressBar(
                    safeSpending,
                    category.minGoal ?: 0.0,
                    category.maxGoal ?: 0.0
                )
            }
    }

    private fun updateProgressBar(current: Double, min: Double, max: Double) {
        spendingProgress.max = max.toInt().coerceAtLeast(1)
        spendingProgress.progress = current.toInt().coerceIn(0, max.toInt())

        val color = when {
            current < min -> ContextCompat.getColor(this, R.color.green)
            current > max -> ContextCompat.getColor(this, R.color.red)
            else -> ContextCompat.getColor(this, R.color.blue)
        }
        spendingProgress.progressTintList = ColorStateList.valueOf(color)
    }

    private fun saveGoals() {
        val category = selectedCategory ?: run {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val min = minGoalInput.text.toString().toDoubleOrNull() ?: run {
            Toast.makeText(this, "Invalid minimum value", Toast.LENGTH_SHORT).show()
            return
        }

        val max = maxGoalInput.text.toString().toDoubleOrNull() ?: run {
            Toast.makeText(this, "Invalid maximum value", Toast.LENGTH_SHORT).show()
            return
        }

        if (min > max) {
            Toast.makeText(this, "Minimum cannot exceed maximum", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            categoryViewModel.updateGoals(category.id, min, max)
            runOnUiThread {
                Toast.makeText(
                    this@SetGoalsActivity,
                    "Goals updated for ${category.name}",
                    Toast.LENGTH_SHORT
                ).show()
                updateUIForSelectedCategory()
            }
        }
    }
}
