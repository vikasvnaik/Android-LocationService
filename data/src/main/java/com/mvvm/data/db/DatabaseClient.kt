package com.mvvm.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class DatabaseClient {
    //our app database object
    //private var appDatabase: AppDatabase? = null
    companion object {
        /*private var mInstance: DatabaseClient? = null
        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance
        }*/
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "TrackerData.db"
        fun getDatabase(context: Context): AppDatabase {

            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                            //.allowMainThreadQueries() // Uncomment if you don't want to use RxJava or coroutines just yet (blocks UI thread)
                            //.addMigrations(Migration_1_2, Migration_2_3)
                            .build()
                    }
                }
            }

            return INSTANCE!!
        }

        private val Migration_1_2: Migration =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "ALTER TABLE startTrip "
                                + " ADD COLUMN createdDate TEXT NOT NULL DEFAULT ''"
                    )
                }
            }

        private val Migration_2_3: Migration =
            object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "ALTER TABLE startTrip "
                                + " ADD COLUMN images TEXT NOT NULL DEFAULT ''"
                    )
                }
            }
    }
    /*init {
        //creating the app database with Room database builder
//MyToDos is the name of the database
        appDatabase =
            Room.databaseBuilder(mCtx, AppDatabase::class.java, "DriverData").build()
    }

    fun getAppDatabase(): AppDatabase? {
        return appDatabase
    }*/
}