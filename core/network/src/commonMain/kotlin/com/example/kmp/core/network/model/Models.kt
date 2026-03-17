package com.example.kmp.core.network.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// News Models
@Serializable
data class NewsResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<NewsItem>
)

@Serializable
data class NewsItem(
    val id: Int,
    val title: String,
    val summary: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("published_at")
    val publishedAt: String,
    @SerialName("news_site")
    val newsSite: String,
    val url: String,
    val authors: List<Author> = emptyList(),
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class Author(
    val name: String,
    val socials: Socials? = null
)

@Serializable
data class Socials(
    val x: String? = null,
    val youtube: String? = null,
    val instagram: String? = null,
    val linkedin: String? = null,
    val mastodon: String? = null,
    val bluesky: String? = null
)

// User Models
@Serializable
data class NetworkUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String
)
