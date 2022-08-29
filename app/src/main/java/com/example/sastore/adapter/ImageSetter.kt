package com.example.sastore.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference

private const val TAG = "ImageSetter"

class ImageSetter {
    companion object {
        @BindingAdapter("setImageResourceFromUrl")
        @JvmStatic
        public fun setImageResourceFromUrl(imageView: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .error(android.R.drawable.alert_dark_frame)
                    .into(imageView)
                //SaveImage(imageView,listOf(imageUrl)).main()
            } else {
                Glide.with(imageView.context)
                    .load(android.R.drawable.ic_dialog_alert)
                    .into(imageView)
            }
        }

internal class SaveImage(val imageView: ImageView,val list: List<String>) {
    fun main() = runBlocking {
        doWorld(imageView,list)
    }

    suspend fun doWorld(imageView: ImageView,list: List<String>) = coroutineScope {  // this: CoroutineScope
        launch {
            doInBackground(imageView.context,list)
        }
    }
    public suspend fun doInBackground(context: Context, vararg params: List<String>) = coroutineScope {
        launch {
            val mContext: WeakReference<Context> = WeakReference(context)
            val url = params[0]
            val requestOptions = RequestOptions().override(100)
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)

            mContext.get()?.let {
                val bitmap = Glide.with(it)
                    .asBitmap()
                    .load(url)
                    .apply(requestOptions)
                    .submit()
                    .get()

                try {
                    var file = it.getDir("Images", Context.MODE_PRIVATE)
                    file = File(file, "img.jpg")
                    val out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    out.flush()
                    out.close()
                    Log.i("Seiggailion", "Image saved.")
                } catch (e: Exception) {
                    Log.i("Seiggailion", "Failed to save image.")
                }
            }
        }

    }
}
    }
}