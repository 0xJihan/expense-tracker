package com.jihan.expensetracker.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface InfoDao {

    @Insert
    suspend fun insertInfo(information: Information)


    @Update
    suspend fun updateInfo(information: Information)

    @Delete
    suspend fun deleteInfo(information: Information)

    @Query("SELECT * FROM information_table ORDER BY id DESC")
    fun getInfo(): LiveData<List<Information>>

    @Query("SELECT * FROM information_table ORDER BY id DESC LIMIT 10")
     fun getRecentInfo(): LiveData<List<Information>>

     @Query("DELETE  FROM information_table")
     suspend fun deleteAll()

     @Query("SELECT * FROM information_table where isIncome = 1 ORDER BY ID DESC")
      fun getIncomeList() : LiveData<List<Information>>

     @Query("SELECT * FROM information_table where isIncome = 0 ORDER BY ID DESC")
      fun getExpenseList() : LiveData<List<Information>>


}