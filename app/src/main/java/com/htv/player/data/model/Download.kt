package com.htv.player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: Int,
    val episodeId: Int,
    val title: String,
    val poster: String,
    val episodeTitle: String,
    val quality: String,
    val estimatedSize: Long,
    val url: String,
    val downloadPath: String,
    val status: DownloadStatus = DownloadStatus.WAITING,
    val progress: Int = 0,
    val downloadedSize: Long = 0,
    val totalSize: Long = 0,
    val downloadSpeed: Long = 0,
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis(),
    val storagePath: String
)

enum class DownloadStatus {
    WAITING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class StorageInfo(
    val path: String,
    val name: String,
    val totalSize: Long,
    val availableSize: Long,
    val isExternal: Boolean
) {
    val usedSize: Long get() = totalSize - availableSize
    val usedPercentage: Float get() = (usedSize.toFloat() / totalSize.toFloat()) * 100
    val isLowSpace: Boolean get() = (availableSize.toFloat() / totalSize.toFloat()) < 0.1f
}

data class DownloadConfig(
    val quality: String,
    val storagePath: String,
    val maxConcurrentDownloads: Int = 2,
    val autoDeleteAfterWatch: Boolean = false
)
