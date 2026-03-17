package com.example.kmp.core.network

import com.example.kmp.core.network.model.NewsResponse
import com.example.kmp.core.network.model.NetworkUser
import com.example.kmp.core.network.model.NewsItem

interface ApiService {
    suspend fun fetchNews(limit: Int = 10, offset: Int = 0): NewsResponse
    suspend fun fetchNewsById(id: Int): NewsItem
    suspend fun fetchUsers(): List<NetworkUser>
}
