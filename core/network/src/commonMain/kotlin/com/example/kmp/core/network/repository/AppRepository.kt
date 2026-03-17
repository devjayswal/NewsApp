package com.example.kmp.core.network.repository

import com.example.kmp.core.network.ApiService
import com.example.kmp.core.network.model.NetworkUser
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.model.NewsResponse
import com.example.kmp.core.utils.AppResult
import kotlinx.serialization.json.Json

class AppRepository(
    private val apiService: ApiService,
    private val localAssetDataSource: LocalAssetDataSource
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getNews(limit: Int = 10, offset: Int = 0): AppResult<List<NewsItem>> {
        return try {
            val response = apiService.fetchNews(limit, offset)
            AppResult.Success(response.results)
        } catch (networkError: Throwable) {
            try {
                // If it's the first page, we can fall back to assets. 
                // For subsequent pages, assets won't have the data.
                if (offset == 0) {
                    val cached = json.decodeFromString<NewsResponse>(localAssetDataSource.getNewsFromAssets())
                    AppResult.Success(cached.results)
                } else {
                    AppResult.Error("No more news available offline", networkError)
                }
            } catch (assetError: Throwable) {
                AppResult.Error("Failed to load news", networkError)
            }
        }
    }

    suspend fun getNewsById(id: Int): AppResult<NewsItem> {
        return try {
            val response = apiService.fetchNewsById(id)
            AppResult.Success(response)
        } catch (e: Exception) {
            AppResult.Error("Failed to load news detail: ${e.message}")
        }
    }

    suspend fun getUsers(): AppResult<List<NetworkUser>> {
        return try {
            AppResult.Success(apiService.fetchUsers())
        } catch (networkError: Throwable) {
            try {
                val cached = json.decodeFromString<List<NetworkUser>>(localAssetDataSource.getUsersFromAssets())
                AppResult.Success(cached)
            } catch (assetError: Throwable) {
                AppResult.Error("Failed to load users", networkError)
            }
        }
    }

    suspend fun saveNews(newsItem: NewsItem) {
        // Here you would implement the logic to save the news item to a local database or file.
        // This is just a placeholder to show where that logic would go.
        println("Saving news item: ${newsItem.title}")
    }
}
