package com.example.budgettrackerapp111.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val minGoal: Double = 0.0,
    val maxGoal: Double = 0.0
)
