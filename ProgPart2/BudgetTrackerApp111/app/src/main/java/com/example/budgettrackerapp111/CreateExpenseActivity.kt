package com.example.budgettrackerapp111

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budgettrackerapp111.data.AppDatabase
import com.example.budgettrackerapp111.data.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreateExpenseActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_expense)

        val titleEditText = findViewById<EditText>(R.id.expense_title)
        val amountEditText = findViewById<EditText>(R.id.expense_amount)
        val dateEditText = findViewById<EditText>(R.id.expense_date)
        val startTimeEditText = findViewById<EditText>(R.id.expense_start_time)
        val endTimeEditText = findViewById<EditText>(R.id.expense_end_time)
        val descriptionEditText = findViewById<EditText>(R.id.expense_description)
        categorySpinner = findViewById(R.id.expense_category_spinner)
        val photoButton = findViewById<Button>(R.id.select_photo_button)
        val saveButton = findViewById<Button>(R.id.save_expense_button)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch(Dispatchers.IO) {
            db.categoryDao().getAllCategories().collect { categories ->
                categoryList = categories.map { it.name }
                runOnUiThread {
                    val adapter = ArrayAdapter(this@CreateExpenseActivity, android.R.layout.simple_spinner_item, categoryList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter
                }
            }
        }

        // Date Picker
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                dateEditText.setText(selectedDate)
            }, year, month, day).show()
        }

        // Time Picker for Start Time
        startTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                val time = String.format("%02d:%02d", h, m)
                startTimeEditText.setText(time)
            }, hour, minute, true).show()
        }

        // Time Picker for End Time
        endTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                val time = String.format("%02d:%02d", h, m)
                endTimeEditText.setText(time)
            }, hour, minute, true).show()
        }

        photoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val date = dateEditText.text.toString().trim()
            val startTime = startTimeEditText.text.toString().trim()
            val endTime = endTimeEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val category = categorySpinner.selectedItem?.toString() ?: ""

            if (title.isEmpty() || amount == null || date.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val expense = Expense(
                title = title,
                amount = amount,
                category = category,
              // <-- Add this line
                date = date,
                startTime = startTime,
                endTime = endTime,
                description = description,
                photoUri = selectedImageUri?.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.expenseDao().insertExpense(expense)
                runOnUiThread {
                    Toast.makeText(this@CreateExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }
}
