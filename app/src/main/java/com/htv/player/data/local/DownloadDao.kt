package com.htv.player.data.local

import androidx.room.*
import com.htv.player.data.model.DownloadTask
import com.htv.player.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createTime DESC")
    fun getAllDownloads(): Flow<List<DownloadTask>>

    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY createTime DESC")
    fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadTask>>

    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY createTime DESC")
    suspend fun getDownloadsByStatusSync(status: DownloadStatus): List<DownloadTask>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadTask?

    @Query("SELECT * FROM downloads WHERE videoId = :videoId AND episodeId = :episodeId")
    suspend fun getDownload(videoId: Int, episodeId: Int): DownloadTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadTask): Long

    @Update
    suspend fun updateDownload(download: DownloadTask)

    @Delete
    suspend fun deleteDownload(download: DownloadTask)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: Long)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateDownloadStatus(id: Long, status: DownloadStatus)

    @Query("UPDATE downloads SET progress = :progress, downloadedSize = :downloadedSize WHERE id = :id")
    suspend fun updateDownloadProgress(id: Long, progress: Int, downloadedSize: Long)

    @Query("UPDATE downloads SET downloadSpeed = :speed WHERE id = :id")
    suspend fun updateDownloadSpeed(id: Long, speed: Long)

    @Query("SELECT SUM(downloadedSize) FROM downloads WHERE status = :status")
    suspend fun getTotalDownloadedSize(status: DownloadStatus = DownloadStatus.COMPLETED): Long?

    @Query("DELETE FROM downloads WHERE status = :status")
    suspend fun deleteDownloadsByStatus(status: DownloadStatus)
}
