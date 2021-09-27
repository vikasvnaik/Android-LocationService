package com.mvvm.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mvvm.data.db.entity.LocationEntity

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY timeStamp ASC")
    suspend fun getAll(): List<LocationEntity>

    @Insert
    suspend fun insert(vararg locationEntity: LocationEntity)

    @Delete
    suspend fun delete(locationEntity: LocationEntity)

    @Query("DELETE FROM locations WHERE id IN (SELECT id FROM locations ORDER BY timeStamp ASC LIMIT :count)")
    suspend fun deleteByCount(count: Int )
}