package com.example.kmp.core.network

import android.content.Context
import com.example.kmp.core.network.repository.LocalAssetDataSource

class AndroidAssetDataSource(private val context: Context) : LocalAssetDataSource {
    override fun getNewsFromAssets(): String =
        context.assets.open("news.json").bufferedReader().use { it.readText() }
    
    override fun getUsersFromAssets(): String =
        context.assets.open("users.json").bufferedReader().use { it.readText() }
}
