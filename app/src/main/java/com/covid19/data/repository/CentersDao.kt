package com.covid19.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.covid19.data.model.Centers

@Dao
interface CentersDao {

    // Centers Table 데이터 추가
    @Insert(onConflict = OnConflictStrategy.IGNORE) // 기존 데이터 무시하고 데이터 추가
    suspend fun insert(centers: Centers)

    // Centers Table 데이터 전부 로드
    @Query("SELECT * FROM Centers")
    suspend fun getAll(): List<Centers>
}