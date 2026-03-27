package com.htv.player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_history")
data class PlayHistory(
    @PrimaryKey
    val videoId: Int,
    val title: String,
    val poster: String,
    val episodeId: Int,
    val episodeTitle: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val progress: Long,
    val duration: Long,
    val lastPlayTime: Long = System.currentTimeMillis()
) {
    val progressPercentage: Float
        get() = if (duration > 0) (progress.toFloat() / duration.toFloat()) * 100 else 0f
}

data class PlaybackState(
    val videoId: Int,
    val episodeId: Int,
    val position: Long,
    val duration: Long,
    val quality: String,
    val subtitleEnabled: Boolean,
    val subtitleTrack: Int,
    val audioTrack: Int,
    val playbackSpeed: Float
)
