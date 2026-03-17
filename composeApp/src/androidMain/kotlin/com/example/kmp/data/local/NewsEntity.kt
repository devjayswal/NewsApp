package com.example.kmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_news")
data class NewsEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val publishedAt: String,
    val newsSite: String,
    val url: String
)
