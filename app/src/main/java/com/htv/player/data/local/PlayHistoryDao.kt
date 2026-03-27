package com.htv.player.data.local

import androidx.room.*
import com.htv.player.data.model.PlayHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {
    @Query("SELECT * FROM play_history ORDER BY lastPlayTime DESC")
    fun getAllHistory(): Flow<List<PlayHistory>>

    @Query("SELECT * FROM play_history ORDER BY lastPlayTime DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 10): Flow<List<PlayHistory>>

    @Query("SELECT * FROM play_history WHERE videoId = :videoId")
    suspend fun getHistoryByVideoId(videoId: Int): PlayHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PlayHistory)

    @Update
    suspend fun updateHistory(history: PlayHistory)

    @Delete
    suspend fun deleteHistory(history: PlayHistory)

    @Query("DELETE FROM play_history WHERE videoId = :videoId")
    suspend fun deleteHistoryByVideoId(videoId: Int)

    @Query("DELETE FROM play_history")
    suspend fun clearAllHistory()

    @Query("UPDATE play_history SET progress = :progress, lastPlayTime = :lastPlayTime WHERE videoId = :videoId")
    suspend fun updateProgress(videoId: Int, progress: Long, lastPlayTime: Long = System.currentTimeMillis())
}
