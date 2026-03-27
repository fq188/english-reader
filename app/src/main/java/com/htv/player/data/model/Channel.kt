package com.htv.player.data.model

data class Channel(
    val id: Int,
    val name: String,
    val logo: String,
    val category: String,
    val sources: List<VideoSource>,
    val currentSource: Int = 0,
    val programName: String? = null,
    val programTime: String? = null
)

data class VideoSource(
    val name: String,
    val url: String,
    val quality: String,
    val isBackup: Boolean = false
)

data class EPGProgram(
    val id: String,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val description: String? = null,
    val category: String? = null
)

data class EPGChannel(
    val channelId: Int,
    val channelName: String,
    val programs: List<EPGProgram>
)
