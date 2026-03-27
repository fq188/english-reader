package com.htv.player.ui.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.Category
import com.htv.player.data.model.Video
import com.htv.player.data.repository.VodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VodViewModel @Inject constructor(
    private val repository: VodRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _latestVideos = MutableStateFlow<List<Video>>(emptyList())
    val latestVideos: StateFlow<List<Video>> = _latestVideos.asStateFlow()

    private val _hotVideos = MutableStateFlow<List<Video>>(emptyList())
    val hotVideos: StateFlow<List<Video>> = _hotVideos.asStateFlow()

    private val _highScoreVideos = MutableStateFlow<List<Video>>(emptyList())
    val highScoreVideos: StateFlow<List<Video>> = _highScoreVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCategories()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            loadLatestVideos()
            loadHotVideos()
            loadHighScoreVideos()

            _isLoading.value = false
        }
    }

    fun refresh() {
        loadData()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().fold(
                onSuccess = { categoryList ->
                    _categories.value = categoryList
                    if (categoryList.isNotEmpty() && _selectedCategory.value == null) {
                        _selectedCategory.value = categoryList.first()
                    }
                },
                onFailure = {
                    loadDemoCategories()
                }
            )
        }
    }

    private fun loadLatestVideos() {
        viewModelScope.launch {
            repository.getRecommend("latest", 10).fold(
                onSuccess = { videos ->
                    _latestVideos.value = videos
                },
                onFailure = {
                    _latestVideos.value = getDemoVideos()
                }
            )
        }
    }

    private fun loadHotVideos() {
        viewModelScope.launch {
            repository.getRecommend("hot", 10).fold(
                onSuccess = { videos ->
                    _hotVideos.value = videos
                },
                onFailure = {
                    _hotVideos.value = getDemoVideos()
                }
            )
        }
    }

    private fun loadHighScoreVideos() {
        viewModelScope.launch {
            repository.getRecommend("score", 10).fold(
                onSuccess = { videos ->
                    _highScoreVideos.value = videos
                },
                onFailure = {
                    _highScoreVideos.value = getDemoVideos()
                }
            )
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    private fun loadDemoCategories() {
        _categories.value = listOf(
            Category("movie", "电影", com.htv.player.data.model.CategoryType.MOVIE),
            Category("drama", "电视剧", com.htv.player.data.model.CategoryType.DRAMA),
            Category("anime", "动漫", com.htv.player.data.model.CategoryType.ANIME),
            Category("overseas", "海外剧", com.htv.player.data.model.CategoryType.OVERSEAS),
            Category("opera", "戏曲", com.htv.player.data.model.CategoryType.OPERA)
        )
    }

    private fun getDemoVideos(): List<Video> {
        return listOf(
            Video(
                id = 1,
                title = "流浪地球",
                poster = "",
                rating = 8.5f,
                year = 2023,
                synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园..."
            ),
            Video(
                id = 2,
                title = "狂飙",
                poster = "",
                rating = 9.0f,
                year = 2023,
                synopsis = "讲述了京海市一线刑警安欣，与黑恶势力展开的长达二十年的生死搏斗故事..."
            ),
            Video(
                id = 3,
                title = "满江红",
                poster = "",
                rating = 7.8f,
                year = 2023,
                synopsis = "南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈..."
            ),
            Video(
                id = 4,
                title = "深海",
                poster = "",
                rating = 8.2f,
                year = 2023,
                synopsis = "在大海的最深处，藏着所有秘密..."
            ),
            Video(
                id = 5,
                title = "三体",
                poster = "",
                rating = 8.7f,
                year = 2023,
                synopsis = "纳米物理学家汪淼与刑警史强共同揭开了地外文明的三体世界的神秘面纱..."
            ),
            Video(
                id = 6,
                title = "熊出没·伴我熊芯",
                poster = "",
                rating = 7.5f,
                year = 2023,
                synopsis = "一个普通的森林夜晚，对小熊大、粉熊福和小光头强而言，是一次冒险旅程的开始..."
            )
        )
    }
}
