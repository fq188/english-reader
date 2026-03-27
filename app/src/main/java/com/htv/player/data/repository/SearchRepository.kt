package com.htv.player.data.repository

import com.htv.player.data.api.ApiService
import com.htv.player.data.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val searchHistory = mutableListOf<String>()

    suspend fun search(keyword: String): Result<SearchResult> = withContext(Dispatchers.IO) {
        try {
            val result = apiService.search(keyword)
            addToHistory(keyword)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getHistory(): List<String> = searchHistory.toList()

    fun addToHistory(keyword: String) {
        if (keyword.isBlank()) return
        searchHistory.remove(keyword)
        searchHistory.add(0, keyword)
        if (searchHistory.size > MAX_HISTORY_SIZE) {
            searchHistory.removeAt(searchHistory.lastIndex)
        }
    }

    fun clearHistory() {
        searchHistory.clear()
    }

    fun removeFromHistory(keyword: String) {
        searchHistory.remove(keyword)
    }

    fun convertToPinyin(keyword: String): String {
        return PinyinConverter.toPinyinInitials(keyword)
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 20
    }
}

object PinyinConverter {
    private val pinyinMap = mapOf(
        'a' to "阿啊吖嗄腌", 'b' to "八吧嗒啪哔扒蹦", 'c' to "嚓差插叉茬茶搓",
        'd' to "哒大呆歹待戴单担旦氮", 'e' to "额诶屙", 'f' to "发罚伐乏筏",
        'g' to "嘎噶尬旮", 'h' to "哈嗨哼和", 'j' to "几加夹嘉家咖",
        'k' to "咖卡咯", 'l' to "啦喇辣腊", 'm' to "妈吗嘛马",
        'n' to "那呐拿娜", 'p' to "啪趴", 'q' to "七期其起",
        'r' to "然", 's' to "撒萨", 't' to "他她它",
        'w' to "挖娃哇", 'x' to "西希", 'y' to "呀压牙芽",
        'z' to "匝杂咱"
    )

    fun toPinyinInitials(chinese: String): String {
        val result = StringBuilder()
        for (char in chinese) {
            val lower = char.lowercaseChar()
            val initials = pinyinMap[lower]
            if (initials != null && initials.contains(lower)) {
                result.append(lower)
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
}
