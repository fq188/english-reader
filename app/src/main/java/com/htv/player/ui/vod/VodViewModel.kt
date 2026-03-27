package com.htv.player.ui.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.Category
import com.htv.player.data.model.CategoryType
import com.htv.player.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VodViewModel @Inject constructor() : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _allVideos = MutableStateFlow<List<Video>>(emptyList())
    val allVideos: StateFlow<List<Video>> = _allVideos.asStateFlow()

    private val _filteredVideos = MutableStateFlow<List<Video>>(emptyList())
    val filteredVideos: StateFlow<List<Video>> = _filteredVideos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDemoData()
    }

    private fun loadDemoData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = listOf(
                    Category("all", "全部", CategoryType.MOVIE),
                    Category("movie", "电影", CategoryType.MOVIE),
                    Category("drama", "电视剧", CategoryType.DRAMA),
                    Category("anime", "动漫", CategoryType.ANIME),
                    Category("overseas", "海外剧", CategoryType.OVERSEAS),
                    Category("opera", "戏曲", CategoryType.OPERA)
                )

                _allVideos.value = listOf(
                    Video(1, "流浪地球", "", rating = 8.5f, year = 2023, categories = listOf("科幻", "冒险"), synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园..."),
                    Video(2, "狂飙", "", rating = 9.0f, year = 2023, categories = listOf("犯罪", "剧情"), synopsis = "讲述了京海市一线刑警安欣，与黑恶势力展开的长达二十年的生死搏斗故事..."),
                    Video(3, "满江红", "", rating = 7.8f, year = 2024, categories = listOf("悬疑", "剧情"), synopsis = "南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈..."),
                    Video(4, "深海", "", rating = 8.2f, year = 2024, categories = listOf("动画", "奇幻"), synopsis = "在大海的最深处，藏着所有秘密..."),
                    Video(5, "三体", "", rating = 8.7f, year = 2023, categories = listOf("科幻", "剧情"), synopsis = "纳米物理学家汪淼与刑警史强共同揭开了地外文明的三体世界的神秘面纱..."),
                    Video(6, "熊出没", "", rating = 7.5f, year = 2024, categories = listOf("动画", "喜剧"), synopsis = "一个普通的森林夜晚，对小熊大、粉熊福和小光头强而言，是一次冒险旅程的开始..."),
                    Video(7, "流浪地球2", "", rating = 8.5f, year = 2024, categories = listOf("科幻", "冒险"), synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器..."),
                    Video(8, "飞驰人生", "", rating = 8.0f, year = 2024, categories = listOf("喜剧", "运动"), synopsis = "曾经叱咤赛车界的张驰，被禁赛五年后重新复出..."),
                    Video(9, "第二十条", "", rating = 7.9f, year = 2024, categories = listOf("剧情", "法律"), synopsis = "基层青年检察官韩明遭遇职业生涯最大危机..."),
                    Video(10, "热辣滚烫", "", rating = 8.1f, year = 2024, categories = listOf("喜剧", "剧情"), synopsis = "乐莹想减肥，她决定去拳击俱乐部..."),
                    Video(11, "你想活出怎样的人生", "", rating = 8.5f, year = 2024, categories = listOf("动画", "奇幻"), synopsis = "少年牧真人的母亲葬身火海后，他随父亲与继母组成新家庭..."),
                    Video(12, "功夫熊猫4", "", rating = 7.8f, year = 2024, categories = listOf("动画", "喜剧"), synopsis = "神龙大侠阿宝回归，发现自己必须找到新的反派..."),
                    Video(13, "哥斯拉大战金刚2", "", rating = 7.5f, year = 2024, categories = listOf("科幻", "动作"), synopsis = "帝王组织开始深入了解神秘之地..."),
                    Video(14, "被我弄丢的你", "", rating = 7.6f, year = 2024, categories = listOf("爱情", "剧情"), synopsis = "每年高考结束，都会迎来一批离别..."),
                    Video(15, "白蛇浮生", "", rating = 7.8f, year = 2024, categories = listOf("动画", "奇幻"), synopsis = "千年之恋，白蛇传说的全新演绎...")
                )

                _filteredVideos.value = _allVideos.value
                _selectedCategory.value = _categories.value.firstOrNull()
            } catch (e: Exception) {
                _error.value = "加载数据失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        filterVideos(category)
    }

    private fun filterVideos(category: Category) {
        _filteredVideos.value = if (category.id == "all") {
            _allVideos.value
        } else {
            _allVideos.value.filter { video ->
                video.categories.any { cat ->
                    cat.contains(category.name) || category.name.contains(cat)
                }
            }
        }
    }

    fun refresh() {
        loadDemoData()
    }
}
