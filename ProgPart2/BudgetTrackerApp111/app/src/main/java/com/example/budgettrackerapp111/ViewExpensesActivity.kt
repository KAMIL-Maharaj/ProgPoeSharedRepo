package com.example.budgettrackerapp111

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettrackerapp111.adapter.ExpenseAdapter
import com.example.budgettrackerapp111.data.AppDatabase
import com.example.budgettrackerapp111.repository.ExpenseRepository
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModel
import com.example.budgettrackerapp111.viewmodel.ExpenseViewModelFactory
import java.util.*

class ViewExpensesActivity : AppCompatActivity() {
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter

    private var startDate = ""
    private var endDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_expenses)

        // Initialize database + ViewModel with Factory
        val db = AppDatabase.getDatabase(this)
        val repository = ExpenseRepository(db.expenseDao())
        expenseViewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(repository)
        )[ExpenseViewModel::class.java]

        // Setup views
        val recyclerView = findViewById<RecyclerView>(R.id.expenses_recycler)
        val selectedRangeText = findViewById<TextView>(R.id.selected_range_text)
        val startButton = findViewById<Button>(R.id.start_date_button)
        val endButton = findViewById<Button>(R.id.end_date_button)

        adapter = ExpenseAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fun updateData() {
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                selectedRangeText.text = "Showing from $startDate to $endDate"
                expenseViewModel.getExpensesBetween(startDate, endDate).observe(this) { expenses ->
                    adapter.updateData(expenses)
                }
            }
        }

        startButton.setOnClickListener { showDatePicker { date -> startDate = date; updateData() } }
        endButton.setOnClickListener { showDatePicker { date -> endDate = date; updateData() } }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            val date = String.format("%04d-%02d-%02d", y, m + 1, d)
            onDateSelected(date)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}
