package com.htv.player.data.model

data class Video(
    val id: Int,
    val title: String,
    val poster: String,
    val backdrop: String? = null,
    val rating: Float = 0f,
    val year: Int? = null,
    val duration: Int? = null,
    val synopsis: String? = null,
    val director: String? = null,
    val actors: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val source: VideoSource? = null,
    val relatedVideos: List<Video> = emptyList()
)

data class Episode(
    val id: Int,
    val title: String,
    val episodeNumber: Int,
    val seasonNumber: Int = 1,
    val duration: Int? = null,
    val poster: String? = null,
    val sources: List<VideoSource> = emptyList()
)

data class Season(
    val number: Int,
    val title: String,
    val poster: String? = null,
    val episodeCount: Int,
    val episodes: List<Episode> = emptyList()
)

data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val icon: String? = null
)

enum class CategoryType {
    MOVIE,
    DRAMA,
    ANIME,
    OVERSEAS,
    OPERA,
    LIVE_TV
}

data class VodResponse(
    val list: List<Video>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val hasMore: Boolean
)

data class SearchResult(
    val videos: List<Video>,
    val channels: List<Channel>
)
