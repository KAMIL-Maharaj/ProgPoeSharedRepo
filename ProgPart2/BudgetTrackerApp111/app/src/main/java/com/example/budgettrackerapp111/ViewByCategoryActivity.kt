package com.example.budgettrackerapp111

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettrackerapp111.adapter.ExpenseAdapter
import com.example.budgettrackerapp111.data.AppDatabase
import com.example.budgettrackerapp111.data.Category
import com.example.budgettrackerapp111.repository.CategoryRepository
import com.example.budgettrackerapp111.repository.ExpenseRepository
import com.example.budgettrackerapp111.viewmodel.CategoryViewModel
import com.example.budgettrackerapp111.viewmodel.CategoryViewModelFactory
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModel
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ViewByCategoryActivity : AppCompatActivity() {

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: ExpenseAdapter
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryNameText: TextView
    private lateinit var spendingSummaryText: TextView
    private lateinit var spendingProgress: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var btnShowData: Button

    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_by_category)

        initViews()
        setupViewModels()
        setupRecyclerView()
        loadCategories()
        setupDatePickers()
    }

    private fun initViews() {
        categorySpinner = findViewById(R.id.category_spinner)
        categoryNameText = findViewById(R.id.category_name)
        spendingSummaryText = findViewById(R.id.spending_summary)
        spendingProgress = findViewById(R.id.spending_progress)
        recyclerView = findViewById(R.id.expenses_recycler)
        btnStartDate = findViewById(R.id.btn_start_date)
        btnEndDate = findViewById(R.id.btn_end_date)
        btnShowData = findViewById(R.id.btn_show_data)
    }

    private fun setupViewModels() {
        val db = AppDatabase.getDatabase(this)
        expenseViewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(ExpenseRepository(db.expenseDao()))
        )[ExpenseViewModel::class.java]

        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(CategoryRepository(db.categoryDao()))
        )[CategoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(emptyList()) { }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupDatePickers() {
        btnStartDate.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                btnStartDate.text = date
            }
        }

        btnEndDate.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                btnEndDate.text = date
            }
        }

        btnShowData.setOnClickListener {
            val start = selectedStartDate
            val end = selectedEndDate
            if (start != null && end != null && categories.isNotEmpty()) {
                val selectedCategory = categories[categorySpinner.selectedItemPosition].name
                loadCategoryData(selectedCategory, start, end)
            } else {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadCategories() {
        categoryViewModel.allCategories.observe(this) { categoryList ->
            categories = categoryList
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categories.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedStartDate?.let { start ->
                        selectedEndDate?.let { end ->
                            loadCategoryData(categories[position].name, start, end)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun loadCategoryData(categoryName: String, startDate: String, endDate: String) {
        categoryNameText.text = categoryName

        categoryViewModel.getCategoryByName(categoryName).observe(this) { category ->
            if (category == null) return@observe

            expenseViewModel.getCategorySpending(categoryName, startDate, endDate).observe(this) { spending ->
                updateBudgetUI(category, spending ?: 0.0, startDate, endDate)
            }

            expenseViewModel.getExpensesByCategoryAndDate(categoryName, startDate, endDate).observe(this) { expenses ->
                adapter.updateData(expenses)
            }
        }
    }

    private fun updateBudgetUI(category: Category, spending: Double, startDate: String, endDate: String) {
        spendingSummaryText.text = """
            Spent from $startDate to $endDate: $${"%.2f".format(spending)}
            Min Goal: $${"%.2f".format(category.minGoal)}
            Max Goal: $${"%.2f".format(category.maxGoal)}
        """.trimIndent()

        val maxGoal = if (category.maxGoal > 0) category.maxGoal else 1.0
        val progress = (spending / maxGoal * 100).toInt().coerceIn(0, 100)
        spendingProgress.progress = progress
        spendingProgress.progressTintList = ColorStateList.valueOf(
            when {
                spending > category.maxGoal -> ContextCompat.getColor(this, android.R.color.holo_red_light)
                spending > category.minGoal -> ContextCompat.getColor(this, android.R.color.holo_orange_light)
                else -> ContextCompat.getColor(this, android.R.color.holo_green_light)
            }
        )
    }
}