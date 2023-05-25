package com.covid19.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.covid19.data.model.Centers

@Database(entities = [Centers::class], version = 1)
abstract class CentersDatabase : RoomDatabase() {
    abstract fun centersDao(): CentersDao

    companion object {
        private var instance: CentersDatabase? = null

        @Synchronized
        fun getInstance(context: Context): CentersDatabase? {
            if (instance == null) {
                synchronized(CentersDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CentersDatabase::class.java,
                        "Centers_table"
                    ).build()
                }
            }
            return instance
        }
    }
}