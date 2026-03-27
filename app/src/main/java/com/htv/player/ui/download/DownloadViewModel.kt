package com.htv.player.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.DownloadTask
import com.htv.player.data.model.StorageInfo
import com.htv.player.data.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val repository: DownloadRepository
) : ViewModel() {

    private val _activeDownloads = MutableStateFlow<List<DownloadTask>>(emptyList())
    val activeDownloads: StateFlow<List<DownloadTask>> = _activeDownloads.asStateFlow()

    private val _completedDownloads = MutableStateFlow<List<DownloadTask>>(emptyList())
    val completedDownloads: StateFlow<List<DownloadTask>> = _completedDownloads.asStateFlow()

    private val _storageInfo = MutableStateFlow<StorageInfo?>(null)
    val storageInfo: StateFlow<StorageInfo?> = _storageInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDownloads()
        loadStorageInfo()
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            repository.getActiveDownloads().collect { downloads ->
                _activeDownloads.value = downloads
            }
        }

        viewModelScope.launch {
            repository.getCompletedDownloads().collect { downloads ->
                _completedDownloads.value = downloads
            }
        }
    }

    private fun loadStorageInfo() {
        _storageInfo.value = repository.getDefaultStorage()
    }

    fun refresh() {
        loadStorageInfo()
    }

    fun pauseDownload(id: Long) {
        viewModelScope.launch {
            repository.pauseDownload(id)
        }
    }

    fun resumeDownload(id: Long) {
        viewModelScope.launch {
            repository.resumeDownload(id)
        }
    }

    fun deleteDownload(id: Long) {
        viewModelScope.launch {
            repository.deleteDownload(id)
        }
    }

    fun pauseAll() {
        viewModelScope.launch {
            repository.pauseAllDownloads()
        }
    }

    fun resumeAll() {
        viewModelScope.launch {
            repository.resumeAllDownloads()
        }
    }

    fun getStorageList(): List<StorageInfo> = repository.getStorageInfo()
}
