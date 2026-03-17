package com.example.kmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp.data.local.NewsDao
import com.example.kmp.data.local.NewsEntity
import com.example.kmp.core.network.model.NewsItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedNewsViewModel(private val newsDao: NewsDao) : ViewModel() {

    val savedNews: StateFlow<List<NewsItem>> = newsDao.getAllSavedNews()
        .map { entities ->
            entities.map { it.toNewsItem() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveNews(newsItem: NewsItem) {
        viewModelScope.launch {
            newsDao.insertNews(newsItem.toNewsEntity())
        }
    }

    fun deleteNews(newsItem: NewsItem) {
        viewModelScope.launch {
            newsDao.deleteNews(newsItem.toNewsEntity())
        }
    }

    suspend fun isNewsSaved(id: Int): Boolean {
        return newsDao.isNewsSaved(id)
    }

    private fun NewsEntity.toNewsItem(): NewsItem {
        return NewsItem(
            id = id,
            title = title,
            summary = summary,
            imageUrl = imageUrl,
            publishedAt = publishedAt,
            newsSite = newsSite,
            url = url
        )
    }

    private fun NewsItem.toNewsEntity(): NewsEntity {
        return NewsEntity(
            id = id,
            title = title,
            summary = summary,
            imageUrl = imageUrl,
            publishedAt = publishedAt,
            newsSite = newsSite,
            url = url
        )
    }
}
