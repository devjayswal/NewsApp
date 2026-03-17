package com.example.kmp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.core.utils.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentOffset = 0
    private val limit = 10
    private var isLastPage = false

    fun loadNews(isRefresh: Boolean = false) {
        if (_isLoading.value || (isLastPage && !isRefresh)) return

        if (isRefresh) {
            currentOffset = 0
            isLastPage = false
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getNews(limit, currentOffset)) {
                is AppResult.Success -> {
                    val newNews = result.data
                    if (isRefresh) {
                        _news.value = newNews
                    } else {
                        _news.value = _news.value + newNews
                    }
                    
                    if (newNews.size < limit) {
                        isLastPage = true
                    } else {
                        currentOffset += limit
                    }
                }
                is AppResult.Error -> {
                    println(result.message)
                }
                AppResult.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}
