package com.example.kmp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import com.example.kmp.core.network.NetworkModule

class MainApplication : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                // Use the shared OkHttpClient that includes the User-Agent header
                add(OkHttpNetworkFetcherFactory(NetworkModule.okHttpClient))
                add(SvgDecoder.Factory())
                add(GifDecoder.Factory())
            }
            .build()
    }
}
