package com.covid19.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.covid19.data.model.Centers

@Dao
interface CentersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(centers: Centers)
}