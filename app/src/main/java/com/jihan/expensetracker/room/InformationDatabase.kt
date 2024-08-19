package com.jihan.expensetracker.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Information::class], version = 1, exportSchema = false)
@TypeConverters(InfoConverter::class)
abstract class InformationDatabase : RoomDatabase() {

    abstract fun getDao(): InfoDao

    companion object {
        @Volatile
        private var instance: InformationDatabase? = null

        fun getDatabase(context: Context): InformationDatabase {

            synchronized(this) {


                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        InformationDatabase::class.java,
                        "information"
                    ).build()
                }
            }
            return instance!!
        }


    }


}