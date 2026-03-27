package com.htv.player.data.api

import com.htv.player.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("live/channels")
    suspend fun getLiveChannels(): List<Channel>

    @GET("live/epg/{channelId}")
    suspend fun getEPG(
        @Path("channelId") channelId: Int,
        @Query("date") date: String? = null
    ): List<EPGProgram>

    @GET("vod/list")
    suspend fun getVodList(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sort") sort: String? = null
    ): VodResponse

    @GET("vod/detail/{videoId}")
    suspend fun getVideoDetail(
        @Path("videoId") videoId: Int
    ): Video

    @GET("vod/search")
    suspend fun search(
        @Query("keyword") keyword: String
    ): SearchResult

    @GET("vod/recommend")
    suspend fun getRecommend(
        @Query("type") type: String,
        @Query("limit") limit: Int = 10
    ): List<Video>

    @GET("vod/categories")
    suspend fun getCategories(): List<Category>
}
