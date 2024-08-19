package com.jihan.expensetracker.room

import androidx.room.TypeConverter
import java.util.Date


class InfoConverter {


    @TypeConverter
    fun dateToLong(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun longToDate(value: Long): Date {
        return Date(value)
    }


    @TypeConverter
    fun booleanToInt(boolean: Boolean): Int {
        return if (boolean) {
            1
        } else {
            0
        }
    }


    @TypeConverter
    fun intToBoolean(value: Int): Boolean {
        return value == 1
    }


}