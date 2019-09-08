package com.an.droid.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.widget.ImageView
import java.lang.Exception
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.os.Looper
import androidx.collection.LruCache

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {
    @Volatile
    private var instance: T? = null

    fun with(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}

interface ImageLoader

class PhotoLoader private constructor(context: Context /* for file cache */) : ImageLoader {
    companion object : SingletonHolder<PhotoLoader, Context>(::PhotoLoader)

    private val bitmapSize = 75
    private val memorySize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 4
    private var lruCache: LruCache<Int, Bitmap> = object : LruCache<Int, Bitmap>(memorySize) {
        override fun sizeOf(key: Int, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }
    private val executorService: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val handler = Handler(Looper.getMainLooper())
    private var defaultImage = R.drawable.stub_off

    fun showImage(url: String, imageView: ImageView): PhotoLoader {
        imageView.setImageBitmap(null)
        imageView.setBackgroundColor(Color.LTGRAY)
        executorService.submit {
            val bitmap = getImage(url)
            handler.post {
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap)
                else imageView.setImageResource(defaultImage)
            }
        }
        return this
    }

    fun default(resId: Int): PhotoLoader {
        defaultImage = resId
        return this
    }

    private fun getImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        bitmap = lruCache.get(url.hashCode())
        if (bitmap == null) {
            bitmap = getImageFromNetwork(url)
            lruCache.put(url.hashCode(), bitmap ?: return null)
        }
        return bitmap
    }

    private fun getImageFromNetwork(url: String): Bitmap? {
        try {
            return URL(url).downloadBitmap(bitmapSize)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                e.printStackTrace()
            return null
        }
    }
    private fun BitmapFactory.Options.calculateInSampleSize(reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun URL.downloadBitmap(size: Int): Bitmap {
        with(openConnection()) {
            connect()
            val bytes = getInputStream().use { it.readBytes() }
            BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
                inSampleSize = calculateInSampleSize(size, size)
                inJustDecodeBounds = false
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
            }
        }
    }
}