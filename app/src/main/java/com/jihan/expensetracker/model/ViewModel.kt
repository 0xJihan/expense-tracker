package com.jihan.expensetracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jihan.expensetracker.room.Information
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(private val repository: Repository) : ViewModel() {


    val arrayList: LiveData<List<Information>> = repository.getInfo()

    val recentArrayList: LiveData<List<Information>> = repository.getRecentInfo()

    val incomeList : LiveData<List<Information>> = repository.getIncomeList()

    val expenseList : LiveData<List<Information>> = repository.getExpenseList()


    val totalBalance: LiveData<String> = repository.totalBalance
    val totalIncome: LiveData<String> = repository.totalIncome
    val totalExpense: LiveData<String> = repository.totalExpense


    fun insertInfo(information: Information) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertInfo(information)
        }
    }


    fun deleteInfo(information: Information) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInfo(information)
        }
    }

    fun updateInfo(information: Information) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateInfo(information)
        }


    }

}
