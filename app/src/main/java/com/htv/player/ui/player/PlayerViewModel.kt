package com.htv.player.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.PlaybackState
import com.htv.player.data.model.VideoSource
import com.htv.player.data.repository.LiveTvRepository
import com.htv.player.data.repository.PlaybackRepository
import com.htv.player.data.repository.VodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val liveTvRepository: LiveTvRepository,
    private val vodRepository: VodRepository,
    private val playbackRepository: PlaybackRepository
) : ViewModel() {

    private val _currentUrl = MutableLiveData<String?>()
    val currentUrl: LiveData<String?> = _currentUrl

    private val _channelName = MutableStateFlow<String?>(null)
    val channelName: StateFlow<String?> = _channelName.asStateFlow()

    private val _currentProgram = MutableStateFlow<String?>(null)
    val currentProgram: StateFlow<String?> = _currentProgram.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var videoId: Int = -1
    private var episodeId: Int = -1
    private var channelId: Int = -1
    private var isLive: Boolean = false
    private var currentSourceIndex = 0
    private var sources: List<VideoSource> = emptyList()

    fun setPlaybackInfo(videoId: Int, episodeId: Int, channelId: Int, isLive: Boolean) {
        this.videoId = videoId
        this.episodeId = episodeId
        this.channelId = channelId
        this.isLive = isLive

        if (isLive) {
            loadLiveChannel()
        } else {
            loadVodEpisode()
        }
    }

    private fun loadLiveChannel() {
        viewModelScope.launch {
            _isLoading.value = true

            val channel = liveTvRepository.getChannelById(channelId)
            if (channel != null) {
                _channelName.value = channel.name
                _currentProgram.value = channel.programName

                sources = channel.sources
                if (sources.isNotEmpty()) {
                    _currentUrl.value = sources[currentSourceIndex].url
                } else {
                    _currentUrl.value = getDemoLiveUrl()
                }
            } else {
                _channelName.value = "CCTV-1 综合"
                _currentProgram.value = "新闻联播"
                _currentUrl.value = getDemoLiveUrl()
            }

            _isLoading.value = false
        }
    }

    private fun loadVodEpisode() {
        viewModelScope.launch {
            _isLoading.value = true

            vodRepository.getVideoDetail(videoId).fold(
                onSuccess = { video ->
                    val episode = video.episodes.find { it.id == episodeId }
                        ?: video.episodes.firstOrNull()

                    if (episode != null) {
                        sources = episode.sources
                        if (sources.isNotEmpty()) {
                            _currentUrl.value = sources[currentSourceIndex].url
                        } else {
                            _currentUrl.value = getDemoVodUrl()
                        }
                    } else {
                        _currentUrl.value = getDemoVodUrl()
                    }
                },
                onFailure = {
                    _currentUrl.value = getDemoVodUrl()
                }
            )

            _isLoading.value = false
        }
    }

    fun switchToBackupSource() {
        if (sources.size > 1 && currentSourceIndex < sources.size - 1) {
            currentSourceIndex++
            _currentUrl.value = sources[currentSourceIndex].url
        } else {
            _error.value = "无法切换到备用信号源"
        }
    }

    fun playNextEpisode() {
        if (isLive) return

        viewModelScope.launch {
            vodRepository.getVideoDetail(videoId).fold(
                onSuccess = { video ->
                    val currentIndex = video.episodes.indexOfFirst { it.id == episodeId }
                    if (currentIndex >= 0 && currentIndex < video.episodes.size - 1) {
                        val nextEpisode = video.episodes[currentIndex + 1]
                        episodeId = nextEpisode.id
                        loadVodEpisode()
                    }
                },
                onFailure = {}
            )
        }
    }

    fun savePlaybackProgress(position: Long) {
        viewModelScope.launch {
            val state = PlaybackState(
                videoId = videoId,
                episodeId = episodeId,
                position = position,
                duration = 0,
                quality = "auto",
                subtitleEnabled = false,
                subtitleTrack = -1,
                audioTrack = -1,
                playbackSpeed = 1f
            )
            playbackRepository.savePlaybackState(state)
        }
    }

    private fun getDemoLiveUrl(): String {
        return "https://cctvalih5ca.v.myalcdn.com/live/cctv1_2/index.m3u8"
    }

    private fun getDemoVodUrl(): String {
        return "https://vfx.mtime.cn/Video/2019/03/18/mp4/190318225327.webm"
    }
}
