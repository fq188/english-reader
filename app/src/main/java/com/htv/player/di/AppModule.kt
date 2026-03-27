package com.htv.player.di

import android.content.Context
import androidx.room.Room
import com.htv.player.data.api.ApiService
import com.htv.player.data.repository.DownloadRepository
import com.htv.player.data.repository.LiveTvRepository
import com.htv.player.data.repository.PlaybackRepository
import com.htv.player.data.repository.VodRepository
import com.htv.player.data.repository.SearchRepository
import com.htv.player.data.local.AppDatabase
import com.htv.player.data.local.DownloadDao
import com.htv.player.data.local.PlayHistoryDao
import com.htv.player.util.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "htv_player_db"
        ).build()
    }

    @Provides
    fun provideDownloadDao(database: AppDatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    fun providePlayHistoryDao(database: AppDatabase): PlayHistoryDao {
        return database.playHistoryDao()
    }

    @Provides
    @Singleton
    fun provideStorageManager(@ApplicationContext context: Context): StorageManager {
        return StorageManager(context)
    }

    @Provides
    @Singleton
    fun provideLiveTvRepository(apiService: ApiService): LiveTvRepository {
        return LiveTvRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideVodRepository(apiService: ApiService): VodRepository {
        return VodRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(apiService: ApiService): SearchRepository {
        return SearchRepository(apiService)
    }

    @Provides
    @Singleton
    fun providePlaybackRepository(
        playHistoryDao: PlayHistoryDao
    ): PlaybackRepository {
        return PlaybackRepository(playHistoryDao)
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(
        downloadDao: DownloadDao,
        storageManager: StorageManager
    ): DownloadRepository {
        return DownloadRepository(downloadDao, storageManager)
    }
}
