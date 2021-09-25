package com.mvvm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mvvm.data.db.dao.LocationDao
import com.mvvm.data.db.entity.LocationEntity

@Database(entities = [LocationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}