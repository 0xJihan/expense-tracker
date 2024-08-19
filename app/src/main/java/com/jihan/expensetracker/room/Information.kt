package com.jihan.expensetracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "information_table")

data class Information(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val amount: Float,
    val notes: String,
    val isIncome: Boolean,
    val date: Date

)