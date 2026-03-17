package com.example.kmp.core.network

import com.example.kmp.core.network.model.NewsResponse
import com.example.kmp.core.network.model.NetworkUser
import com.example.kmp.core.network.model.NewsItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApiService : ApiService {
    @GET("v4/reports/")
    override suspend fun fetchNews(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NewsResponse

    @GET("v4/reports/{id}/")
    override suspend fun fetchNewsById(@Path("id") id: Int): NewsItem

    override suspend fun fetchUsers(): List<NetworkUser> = emptyList()
}
