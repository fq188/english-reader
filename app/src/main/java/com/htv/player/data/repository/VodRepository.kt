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
            Result.success(getDemoVodResponse())
        }
    }

    suspend fun getVideoDetail(videoId: Int): Result<Video> = withContext(Dispatchers.IO) {
        try {
            val video = apiService.getVideoDetail(videoId)
            Result.success(video)
        } catch (e: Exception) {
            Result.success(getDemoVideo(videoId))
        }
    }

    suspend fun getRecommend(type: String, limit: Int = 10): Result<List<Video>> = withContext(Dispatchers.IO) {
        try {
            val videos = apiService.getRecommend(type, limit)
            Result.success(videos)
        } catch (e: Exception) {
            Result.success(getDemoVideos())
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

    private fun getDemoVodResponse(): VodResponse {
        return VodResponse(
            list = getDemoVideos(),
            page = 1,
            pageSize = 20,
            total = getDemoVideos().size,
            hasMore = false
        )
    }

    fun getDemoVideos(): List<Video> {
        return listOf(
            Video(1, "流浪地球", "", rating = 8.5f, year = 2023,
                synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园...",
                categories = listOf("科幻", "冒险")),
            Video(2, "狂飙", "", rating = 9.0f, year = 2023,
                synopsis = "讲述了京海市一线刑警安欣，与黑恶势力展开的长达二十年的生死搏斗故事...",
                categories = listOf("犯罪", "剧情")),
            Video(3, "满江红", "", rating = 7.8f, year = 2023,
                synopsis = "南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈...",
                categories = listOf("悬疑", "剧情")),
            Video(4, "深海", "", rating = 8.2f, year = 2023,
                synopsis = "在大海的最深处，藏着所有秘密...",
                categories = listOf("动画", "奇幻")),
            Video(5, "三体", "", rating = 8.7f, year = 2023,
                synopsis = "纳米物理学家汪淼与刑警史强共同揭开了地外文明的三体世界的神秘面纱...",
                categories = listOf("科幻", "剧情")),
            Video(6, "熊出没·伴我熊芯", "", rating = 7.5f, year = 2023,
                synopsis = "一个普通的森林夜晚，对小熊大、粉熊福和小光头强而言，是一次冒险旅程的开始...",
                categories = listOf("动画", "喜剧")),
            Video(7, "流浪地球2", "", rating = 8.5f, year = 2023,
                synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器...",
                categories = listOf("科幻", "冒险")),
            Video(8, "满江红", "", rating = 7.8f, year = 2023,
                synopsis = "南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈...",
                categories = listOf("悬疑", "剧情")),
            Video(9, "深海", "", rating = 8.2f, year = 2023,
                synopsis = "在大海的最深处，藏着所有秘密...",
                categories = listOf("动画", "奇幻")),
            Video(10, "三体", "", rating = 8.7f, year = 2023,
                synopsis = "纳米物理学家汪淼与刑警史强共同揭开了地外文明的三体世界的神秘面纱...",
                categories = listOf("科幻", "剧情"))
        )
    }

    private fun getDemoVideo(videoId: Int): Video {
        return Video(
            id = videoId,
            title = "流浪地球2",
            poster = "",
            rating = 8.5f,
            year = 2023,
            duration = 173,
            synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战...",
            director = "郭帆",
            actors = listOf("吴京", "刘德华", "李雪健", "沙溢"),
            categories = listOf("科幻", "冒险", "灾难"),
            episodes = listOf(
                com.htv.player.data.model.Episode(1, "第1集", 1, 1, 45),
                com.htv.player.data.model.Episode(2, "第2集", 2, 1, 45),
                com.htv.player.data.model.Episode(3, "第3集", 3, 1, 45),
                com.htv.player.data.model.Episode(4, "第4集", 4, 1, 45)
            )
        )
    }
}
