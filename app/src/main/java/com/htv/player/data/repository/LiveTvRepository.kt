package com.htv.player.data.repository

import com.htv.player.data.api.ApiService
import com.htv.player.data.model.Channel
import com.htv.player.data.model.EPGChannel
import com.htv.player.data.model.EPGProgram
import com.htv.player.data.model.VideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveTvRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val channels = mutableListOf<Channel>()

    suspend fun getChannels(): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            if (channels.isEmpty()) {
                val channelList = apiService.getLiveChannels()
                channels.clear()
                channels.addAll(channelList)
            }
            Result.success(channels.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChannelById(id: Int): Channel? = withContext(Dispatchers.IO) {
        channels.find { it.id == id }
    }

    suspend fun getEPG(channelId: Int, date: String? = null): Result<List<EPGProgram>> = withContext(Dispatchers.IO) {
        try {
            val programs = apiService.getEPG(channelId, date)
            Result.success(programs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun switchSource(channelId: Int): VideoSource? = withContext(Dispatchers.IO) {
        val channel = channels.find { it.id == channelId } ?: return@withContext null
        val currentIndex = channel.currentSource
        val nextIndex = (currentIndex + 1) % channel.sources.size
        return@withContext if (nextIndex != currentIndex) {
            channel.sources.getOrNull(nextIndex)
        } else null
    }

    fun getCategories(): List<String> {
        return listOf("央视", "卫视", "地方台", "少儿频道", "体育频道", "其他")
    }
}
