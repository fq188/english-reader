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
            Result.success(getDemoChannels())
        }
    }

    suspend fun getChannelById(id: Int): Channel? = withContext(Dispatchers.IO) {
        channels.find { it.id == id } ?: getDemoChannels().find { it.id == id }
    }

    suspend fun getEPG(channelId: Int, date: String? = null): Result<List<EPGProgram>> = withContext(Dispatchers.IO) {
        try {
            val programs = apiService.getEPG(channelId, date)
            Result.success(programs)
        } catch (e: Exception) {
            Result.success(getDemoEPG(channelId))
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

    private fun getDemoChannels(): List<Channel> {
        return listOf(
            Channel(1, "CCTV-1 综合", "", "央视", emptyList(), 0, "新闻联播", "19:00-19:30"),
            Channel(2, "CCTV-2 财经", "", "央视", emptyList(), 0, "经济信息联播", "20:00-21:00"),
            Channel(3, "CCTV-5 体育", "", "体育频道", emptyList(), 0, "体育新闻", "18:00-19:00"),
            Channel(4, "北京卫视", "", "卫视", emptyList(), 0, "电视剧", "19:30-21:00"),
            Channel(5, "东方卫视", "", "卫视", emptyList(), 0, "极限挑战", "21:00-22:30"),
            Channel(6, "浙江卫视", "", "卫视", emptyList(), 0, "奔跑吧", "21:00-22:30"),
            Channel(7, "湖南卫视", "", "卫视", emptyList(), 0, "快乐大本营", "20:00-22:00"),
            Channel(8, "少儿频道", "", "少儿频道", emptyList(), 0, "熊出没", "18:00-19:00")
        )
    }

    private fun getDemoEPG(channelId: Int): List<EPGProgram> {
        return listOf(
            EPGProgram("1", "新闻联播", System.currentTimeMillis(), System.currentTimeMillis() + 1800000, "最新新闻"),
            EPGProgram("2", "天气预报", System.currentTimeMillis() + 1800000, System.currentTimeMillis() + 3600000, "全国天气预报"),
            EPGProgram("3", "焦点访谈", System.currentTimeMillis() + 3600000, System.currentTimeMillis() + 5400000, "深度报道")
        )
    }
}
