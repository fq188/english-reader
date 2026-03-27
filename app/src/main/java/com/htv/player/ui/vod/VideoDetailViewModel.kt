package com.htv.player.ui.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.Video
import com.htv.player.data.repository.VodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    private val repository: VodRepository
) : ViewModel() {

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadVideoDetail(videoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getVideoDetail(videoId).fold(
                onSuccess = { video ->
                    _video.value = video
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _video.value = getDemoVideo(videoId)
                }
            )

            _isLoading.value = false
        }
    }

    private fun getDemoVideo(videoId: Int): Video {
        return Video(
            id = videoId,
            title = "流浪地球2",
            poster = "",
            backdrop = "",
            rating = 8.5f,
            year = 2023,
            duration = 173,
            synopsis = "太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战...",
            director = "郭帆",
            actors = listOf("吴京", "刘德华", "李雪健", "沙溢"),
            categories = listOf("科幻", "冒险", "灾难"),
            episodes = listOf(
                com.htv.player.data.model.Episode(1, "流浪地球2", 1, 1, 173),
                com.htv.player.data.model.Episode(2, "流浪地球2", 2, 1, 173),
                com.htv.player.data.model.Episode(3, "流浪地球2", 3, 1, 173)
            )
        )
    }
}
