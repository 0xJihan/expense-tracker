package com.jihan.expensetracker.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jihan.expensetracker.room.InfoDao
import com.jihan.expensetracker.room.Information

class Repository(private val infoDao: InfoDao) {

    suspend fun insertInfo(information: Information) {
        infoDao.insertInfo(information)
    }


    suspend fun deleteInfo(information: Information) {
        infoDao.deleteInfo(information)
    }

    suspend fun updateInfo(information: Information) {
        infoDao.updateInfo(information)
    }

    fun getInfo(): LiveData<List<Information>> {
        return infoDao.getInfo()
    }

    fun getRecentInfo(): LiveData<List<Information>> {
        return infoDao.getRecentInfo()
    }


    // ============================== calculating total balance ====================================

    private val _totalBalance = MutableLiveData("0.0")
    val totalBalance: LiveData<String> = _totalBalance

    // ============================== calculating total income ====================================

    private val _totalIncome = MutableLiveData<String>("0.0")
    val totalIncome: LiveData<String> = _totalIncome

    //================================= Calculating total expense ================================================

    private val _totalExpense = MutableLiveData<String>("0.0")
    val totalExpense: LiveData<String> = _totalExpense


    init {
        getInfo().observeForever { list ->
            updateTotalIncome(list)
            updateTotalExpense(list)
        }

        // calculating total balance
        _totalIncome.observeForever{
            updateTotalBalance()
        }
        _totalExpense.observeForever{
            updateTotalBalance()
        }

    }


    private fun updateTotalBalance() {
        val totalBalance = _totalIncome.value!!.toFloat() - _totalExpense.value!!.toFloat()
        _totalBalance.value = totalBalance.toString()
    }


    private fun updateTotalIncome(list: List<Information>?) {
        var totalIncome: Float = 0.toFloat()
        if (list != null) {
            for (info in list) {
                if (info.isIncome) {
                    totalIncome += info.amount
                }
            }
        }
        _totalIncome.value = totalIncome.toString()
    }


    private fun updateTotalExpense(list: List<Information>?) {

        var totalExpense = 0.toFloat()

        if (list != null) {
            for (info in list) {
                if (!info.isIncome) {
                    totalExpense += info.amount
                }
            }
        }

        _totalExpense.value = totalExpense.toString()

    }

    fun getIncomeList(): LiveData<List<Information>> {
        return infoDao.getIncomeList()
    }

    fun getExpenseList(): LiveData<List<Information>> {
        return infoDao.getExpenseList()
    }


}