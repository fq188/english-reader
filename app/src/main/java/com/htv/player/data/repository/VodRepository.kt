package com.htv.player.data.repository

import com.htv.player.data.api.ApiService
import com.htv.player.data.model.Category
import com.htv.player.data.model.CategoryType
import com.htv.player.data.model.Video
import com.htv.player.data.model.VodResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VodRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getVodList(
        category: String? = null,
        page: Int = 1,
        pageSize: Int = 20,
        sort: String? = null
    ): Result<VodResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getVodList(category, page, pageSize, sort)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVideoDetail(videoId: Int): Result<Video> = withContext(Dispatchers.IO) {
        try {
            val video = apiService.getVideoDetail(videoId)
            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecommend(type: String, limit: Int = 10): Result<List<Video>> = withContext(Dispatchers.IO) {
        try {
            val videos = apiService.getRecommend(type, limit)
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val categories = apiService.getCategories()
            Result.success(categories)
        } catch (e: Exception) {
            Result.success(getDefaultCategories())
        }
    }

    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category("movie", "电影", CategoryType.MOVIE),
            Category("drama", "电视剧", CategoryType.DRAMA),
            Category("anime", "动漫", CategoryType.ANIME),
            Category("overseas_us", "美剧", CategoryType.OVERSEAS),
            Category("overseas_uk", "英剧", CategoryType.OVERSEAS),
            Category("opera_yuju", "豫剧", CategoryType.OPERA),
            Category("opera_jingju", "京剧", CategoryType.OPERA)
        )
    }
}
