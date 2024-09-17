package com.msdc.baobuzz.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageLoader(private val context: Context) {
    private val memoryCache: LruCache<String, Bitmap>
    private val diskCacheDir: File
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
        diskCacheDir = File(context.cacheDir, "images")
        diskCacheDir.mkdirs()
    }

    fun loadImage(url: String, imageView: ImageView) {
        coroutineScope.launch {
            val bitmap = getBitmapFromCache(url) ?: downloadImage(url)
            bitmap?.let { imageView.setImageBitmap(it) }
        }
    }

    private suspend fun getBitmapFromCache(url: String): Bitmap? {
        return memoryCache[url] ?: getBitmapFromDiskCache(url)
    }

    private suspend fun getBitmapFromDiskCache(url: String): Bitmap? = withContext(Dispatchers.IO) {
        val file = File(diskCacheDir, url.hashCode().toString())
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            connection.disconnect()

            bitmap?.let {
                addBitmapToCache(url, it)
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun addBitmapToCache(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
        withContext(Dispatchers.IO) {
            val file = File(diskCacheDir, url.hashCode().toString())
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
    }

    fun clear() {
        memoryCache.evictAll()
        diskCacheDir.deleteRecursively()
        diskCacheDir.mkdirs()
    }
}