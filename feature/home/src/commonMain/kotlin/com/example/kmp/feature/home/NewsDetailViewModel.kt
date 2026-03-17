package com.example.kmp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp.core.network.model.NewsItem
import com.example.kmp.core.network.repository.AppRepository
import com.example.kmp.core.utils.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsDetailViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _newsDetail = MutableStateFlow<AppResult<NewsItem>>(AppResult.Loading)
    val newsDetail: StateFlow<AppResult<NewsItem>> = _newsDetail

    fun loadNewsDetail(id: Int) {
        viewModelScope.launch {
            _newsDetail.value = AppResult.Loading
            _newsDetail.value = repository.getNewsById(id)
        }
    }
    fun saveNews(newsItem: NewsItem) {
        viewModelScope.launch {
            repository.saveNews(newsItem)
            // You can perform any additional actions here after saving the news

        }
    }
}
