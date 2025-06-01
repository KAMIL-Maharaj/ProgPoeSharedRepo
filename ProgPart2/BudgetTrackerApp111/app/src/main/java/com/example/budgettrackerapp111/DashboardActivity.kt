package com.example.budgettrackerapp111

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView

class DashboardActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Set up window insets for edge-to-edge effect
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the welcome message


        val welcomeText: TextView = findViewById(R.id.welcome_text)
        val username = intent.getStringExtra("username") ?: "User"
        welcomeText.text = "Welcome, $username"


        // Set up the button listeners for navigation
        findViewById<Button>(R.id.manage_categories_button).setOnClickListener {
            // Navigate to Manage Categories screen
            startActivity(Intent(this, ManageCategoriesActivity::class.java))
        }

        findViewById<Button>(R.id.create_expense_button).setOnClickListener {
            // Navigate to Create Expense screen
            startActivity(Intent(this, CreateExpenseActivity::class.java))
        }

        findViewById<Button>(R.id.view_expenses_button).setOnClickListener {
            // Navigate to View Expenses screen
            startActivity(Intent(this, ViewExpensesActivity::class.java))
        }


        findViewById<Button>(R.id.view_by_category_button).setOnClickListener {
            // Navigate to View by Category screen
            startActivity(Intent(this, ViewByCategoryActivity::class.java))
        }


        findViewById<Button>(R.id.set_goals).setOnClickListener {
            // Navigate to View by Category screen
            startActivity(Intent(this, SetGoalsActivity::class.java))
        }
    }
}
