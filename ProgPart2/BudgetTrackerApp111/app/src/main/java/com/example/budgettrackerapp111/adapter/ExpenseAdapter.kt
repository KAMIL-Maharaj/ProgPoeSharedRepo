package com.example.budgettrackerapp111.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettrackerapp111.R
import com.example.budgettrackerapp111.data.Expense

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onItemClick: (Expense) -> Unit = {} // Default to an empty lambda
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.expense_title)
        private val amountView: TextView = itemView.findViewById(R.id.expense_amount)
        private val dateView: TextView = itemView.findViewById(R.id.expense_date)
        private val descriptionView: TextView = itemView.findViewById(R.id.expense_desc)

        fun bind(expense: Expense) {
            titleView.text = expense.title
            amountView.text = "$${String.format("%.2f", expense.amount)}"
            dateView.text = expense.date
            descriptionView.text = expense.description // Corrected this line to set the text

            // Set up the click listener on the item
            itemView.setOnClickListener { onItemClick(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
