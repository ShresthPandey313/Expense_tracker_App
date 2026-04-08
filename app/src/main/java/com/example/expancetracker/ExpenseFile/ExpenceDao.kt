package com.example.expancetracker.ExpenseFile

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface ExpenceDao {
    @Insert
    suspend fun insert(expense: Expence)

    @Update
    suspend fun update(expense: Expence)

    @Delete
    fun delete(expense: Expence)

    @Query("SELECT * FROM expenses_table ORDER BY id DESC")
    suspend fun getAllExpensesOrderById(): List<Expence>

    @Query("SELECT * FROM expenses_table ORDER BY merchant DESC")
    suspend fun getAllExpensesOrderByMerchant(): List<Expence>

    @Query("SELECT * FROM expenses_table ORDER BY amount DESC")
    suspend fun getAllExpensesOrderByAmount(): List<Expence>






}