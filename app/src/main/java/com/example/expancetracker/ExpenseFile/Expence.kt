package com.example.expancetracker.ExpenseFile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses_table")
data class Expence(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val amount: Double,
    val merchant: String,
    val purpose: String,
    val timestamp: Long
)