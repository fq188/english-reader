package com.htv.player.data.repository

import com.htv.player.data.local.DownloadDao
import com.htv.player.data.model.DownloadStatus
import com.htv.player.data.model.DownloadTask
import com.htv.player.data.model.StorageInfo
import com.htv.player.util.StorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadDao: DownloadDao,
    private val storageManager: StorageManager
) {
    fun getAllDownloads(): Flow<List<DownloadTask>> = downloadDao.getAllDownloads()

    fun getActiveDownloads(): Flow<List<DownloadTask>> {
        return downloadDao.getDownloadsByStatus(DownloadStatus.DOWNLOADING)
            .map { active ->
                val waiting = downloadDao.getDownloadsByStatusSync(DownloadStatus.WAITING)
                active + waiting
            }
    }

    fun getCompletedDownloads(): Flow<List<DownloadTask>> {
        return downloadDao.getDownloadsByStatus(DownloadStatus.COMPLETED)
    }

    suspend fun getDownloadById(id: Long): DownloadTask? = withContext(Dispatchers.IO) {
        downloadDao.getDownloadById(id)
    }

    suspend fun getDownload(videoId: Int, episodeId: Int): DownloadTask? = withContext(Dispatchers.IO) {
        downloadDao.getDownload(videoId, episodeId)
    }

    suspend fun createDownload(task: DownloadTask): Long = withContext(Dispatchers.IO) {
        downloadDao.insertDownload(task)
    }

    suspend fun pauseDownload(id: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.PAUSED)
    }

    suspend fun resumeDownload(id: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.WAITING)
    }

    suspend fun pauseAllDownloads() = withContext(Dispatchers.IO) {
        val downloading = downloadDao.getDownloadsByStatusSync(DownloadStatus.DOWNLOADING)
        val waiting = downloadDao.getDownloadsByStatusSync(DownloadStatus.WAITING)
        (downloading + waiting).forEach {
            downloadDao.updateDownloadStatus(it.id, DownloadStatus.PAUSED)
        }
    }

    suspend fun resumeAllDownloads() = withContext(Dispatchers.IO) {
        val paused = downloadDao.getDownloadsByStatusSync(DownloadStatus.PAUSED)
        paused.forEach {
            downloadDao.updateDownloadStatus(it.id, DownloadStatus.WAITING)
        }
    }

    suspend fun deleteDownload(id: Long) = withContext(Dispatchers.IO) {
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            storageManager.deleteFile(download.downloadPath)
        }
        downloadDao.deleteDownloadById(id)
    }

    suspend fun updateProgress(id: Long, progress: Int, downloadedSize: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadProgress(id, progress, downloadedSize)
    }

    suspend fun updateSpeed(id: Long, speed: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadSpeed(id, speed)
    }

    suspend fun markCompleted(id: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.COMPLETED)
    }

    suspend fun markFailed(id: Long) = withContext(Dispatchers.IO) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.FAILED)
    }

    fun getStorageInfo(): List<StorageInfo> = storageManager.getStorageList()

    fun getDefaultStorage(): StorageInfo? = storageManager.getDefaultStorage()

    fun isExternalStorageAvailable(): Boolean = storageManager.isExternalStorageAvailable()

    fun isStorageRemoving(path: String): Boolean = storageManager.isStorageRemoving(path)
}
