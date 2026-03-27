package com.htv.player.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htv.player.data.model.SearchResult
import com.htv.player.data.model.Video
import com.htv.player.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow(SearchResult(emptyList(), emptyList()))
    val searchResults: StateFlow<SearchResult> = _searchResults.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery = ""

    init {
        loadHistory()
    }

    private fun loadHistory() {
        _searchHistory.value = repository.getHistory()
    }

    fun setSearchQuery(query: String) {
        currentQuery = query
        if (query.isBlank()) {
            _searchResults.value = SearchResult(emptyList(), emptyList())
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            performSearch(query)
        }
    }

    fun search(query: String) {
        currentQuery = query
        viewModelScope.launch {
            _isLoading.value = true
            performSearch(query)
            _isLoading.value = false
        }
    }

    private suspend fun performSearch(query: String) {
        val pinyinQuery = repository.convertToPinyin(query)

        repository.search(query).fold(
            onSuccess = { result ->
                _searchResults.value = result
            },
            onFailure = {
                _searchResults.value = SearchResult(getDemoResults(query), emptyList())
            }
        )
    }

    fun clearHistory() {
        repository.clearHistory()
        _searchHistory.value = emptyList()
    }

    private fun getDemoResults(query: String): List<Video> {
        val allVideos = listOf(
            Video(1, "流浪地球", "", rating = 8.5f, year = 2023),
            Video(2, "狂飙", "", rating = 9.0f, year = 2023),
            Video(3, "满江红", "", rating = 7.8f, year = 2023),
            Video(4, "深海", "", rating = 8.2f, year = 2023),
            Video(5, "三体", "", rating = 8.7f, year = 2023),
            Video(6, "熊出没·伴我熊芯", "", rating = 7.5f, year = 2023)
        )

        return allVideos.filter {
            it.title.contains(query, ignoreCase = true) ||
            repository.convertToPinyin(it.title).contains(repository.convertToPinyin(query), ignoreCase = true)
        }
    }
}
