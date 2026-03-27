package com.htv.player.ui.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.Channel
import com.htv.player.data.repository.LiveTvRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveTvViewModel @Inject constructor(
    private val repository: LiveTvRepository
) : ViewModel() {

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _categories.value = repository.getCategories()
        if (_categories.value.isNotEmpty()) {
            _selectedCategory.value = _categories.value.first()
        }
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getChannels().fold(
                onSuccess = { channelList ->
                    val filtered = _selectedCategory.value?.let { category ->
                        if (category == "其他") {
                            channelList
                        } else {
                            channelList.filter { it.category == category }
                        }
                    } ?: channelList

                    _channels.value = filtered
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    loadDemoChannels()
                }
            )

            _isLoading.value = false
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadChannels()
        }
    }

    private fun loadDemoChannels() {
        val demoChannels = listOf(
            Channel(
                id = 1,
                name = "CCTV-1 综合",
                logo = "",
                category = "央视",
                sources = emptyList(),
                programName = "新闻联播",
                programTime = "19:00-19:30"
            ),
            Channel(
                id = 2,
                name = "CCTV-2 财经",
                logo = "",
                category = "央视",
                sources = emptyList(),
                programName = "经济信息联播",
                programTime = "20:00-21:00"
            ),
            Channel(
                id = 3,
                name = "CCTV-5 体育",
                logo = "",
                category = "体育频道",
                sources = emptyList(),
                programName = "体育新闻",
                programTime = "18:00-19:00"
            ),
            Channel(
                id = 4,
                name = "北京卫视",
                logo = "",
                category = "卫视",
                sources = emptyList(),
                programName = "电视剧",
                programTime = "19:30-21:00"
            ),
            Channel(
                id = 5,
                name = "东方卫视",
                logo = "",
                category = "卫视",
                sources = emptyList(),
                programName = "极限挑战",
                programTime = "21:00-22:30"
            ),
            Channel(
                id = 6,
                name = "浙江卫视",
                logo = "",
                category = "卫视",
                sources = emptyList(),
                programName = "奔跑吧",
                programTime = "21:00-22:30"
            ),
            Channel(
                id = 7,
                name = "湖南卫视",
                logo = "",
                category = "卫视",
                sources = emptyList(),
                programName = "快乐大本营",
                programTime = "20:00-22:00"
            ),
            Channel(
                id = 8,
                name = "少儿频道",
                logo = "",
                category = "少儿频道",
                sources = emptyList(),
                programName = "熊出没",
                programTime = "18:00-19:00"
            )
        )
        _channels.value = demoChannels
    }
}
