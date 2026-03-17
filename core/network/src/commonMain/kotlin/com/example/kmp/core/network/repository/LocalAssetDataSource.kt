package com.example.kmp.core.network.repository

interface LocalAssetDataSource {
    fun getNewsFromAssets(): String
    fun getUsersFromAssets(): String
}

