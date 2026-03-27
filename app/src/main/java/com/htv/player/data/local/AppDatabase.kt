package com.htv.player.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.htv.player.data.model.DownloadTask
import com.htv.player.data.model.PlayHistory

@Database(
    entities = [DownloadTask::class, PlayHistory::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun playHistoryDao(): PlayHistoryDao
}
