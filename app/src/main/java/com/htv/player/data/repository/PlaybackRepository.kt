package com.htv.player.data.repository

import com.htv.player.data.local.PlayHistoryDao
import com.htv.player.data.model.PlayHistory
import com.htv.player.data.model.PlaybackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepository @Inject constructor(
    private val playHistoryDao: PlayHistoryDao
) {
    fun getAllHistory(): Flow<List<PlayHistory>> = playHistoryDao.getAllHistory()

    fun getRecentHistory(limit: Int = 10): Flow<List<PlayHistory>> = playHistoryDao.getRecentHistory(limit)

    suspend fun getHistoryByVideoId(videoId: Int): PlayHistory? = withContext(Dispatchers.IO) {
        playHistoryDao.getHistoryByVideoId(videoId)
    }

    suspend fun savePlaybackState(state: PlaybackState) = withContext(Dispatchers.IO) {
        val history = PlayHistory(
            videoId = state.videoId,
            title = "",
            poster = "",
            episodeId = state.episodeId,
            episodeTitle = "",
            seasonNumber = 1,
            episodeNumber = 1,
            progress = state.position,
            duration = state.duration
        )
        playHistoryDao.insertHistory(history)
    }

    suspend fun updateProgress(videoId: Int, progress: Long, duration: Long) = withContext(Dispatchers.IO) {
        val existing = playHistoryDao.getHistoryByVideoId(videoId)
        if (existing != null) {
            playHistoryDao.updateProgress(videoId, progress)
        } else {
            playHistoryDao.insertHistory(
                PlayHistory(
                    videoId = videoId,
                    title = "",
                    poster = "",
                    episodeId = 0,
                    episodeTitle = "",
                    seasonNumber = 1,
                    episodeNumber = 1,
                    progress = progress,
                    duration = duration
                )
            )
        }
    }

    suspend fun deleteHistory(videoId: Int) = withContext(Dispatchers.IO) {
        playHistoryDao.deleteHistoryByVideoId(videoId)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        playHistoryDao.clearAllHistory()
    }
}
