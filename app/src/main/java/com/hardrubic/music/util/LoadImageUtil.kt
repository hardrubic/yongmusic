package com.hardrubic.music.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions.*
import com.hardrubic.music.R

object LoadImageUtil {

    @JvmOverloads
    fun loadFromNetwork(context: Context, url: String, imageView: ImageView) {
        val requestBuilder = Glide.with(context).load(Uri.parse(url))
        loadImage(requestBuilder, imageView)
    }

    private fun loadImage(requestBuilder: RequestBuilder<Drawable>, imageView: ImageView) {
        requestBuilder
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(placeholderOf(R.mipmap.ic_empty_cover))
                .apply(errorOf(R.mipmap.ic_empty_cover))
        requestBuilder.into(imageView)
    }
}