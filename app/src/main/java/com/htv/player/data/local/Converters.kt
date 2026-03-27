package com.htv.player.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.htv.player.data.model.DownloadStatus
import com.htv.player.data.model.VideoSource

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromVideoSourceList(value: List<VideoSource>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoSourceList(value: String): List<VideoSource> {
        val listType = object : TypeToken<List<VideoSource>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus {
        return DownloadStatus.valueOf(value)
    }
}
