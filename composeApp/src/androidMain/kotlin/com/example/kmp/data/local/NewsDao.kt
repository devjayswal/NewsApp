package com.example.kmp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM saved_news")
    fun getAllSavedNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Delete
    suspend fun deleteNews(news: NewsEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news WHERE id = :id)")
    suspend fun isNewsSaved(id: Int): Boolean
}
