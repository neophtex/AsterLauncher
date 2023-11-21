package com.series.aster.launcher.data


import androidx.room.Database
import androidx.room.RoomDatabase
import com.series.aster.launcher.data.dao.AppInfoDAO
import com.series.aster.launcher.data.entities.AppInfo

@Database(entities = [AppInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppInfoDAO
}