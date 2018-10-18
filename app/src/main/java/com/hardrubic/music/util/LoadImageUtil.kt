package com.hardrubic.music.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf
import com.bumptech.glide.request.RequestOptions.errorOf
import com.hardrubic.music.R


object LoadImageUtil {

    fun loadFromNetwork(context: Context, url: String, imageView: ImageView, requestOptions: RequestOptions? = null) {
        val requestBuilder = Glide.with(context).load(Uri.parse(url))
        loadImage(requestBuilder, imageView, requestOptions)
    }

    fun loadFromResource(context: Context, @DrawableRes resourceId: Int, imageView: ImageView, requestOptions: RequestOptions? = null) {
        val requestBuilder = Glide.with(context).load(resourceId)
        loadImage(requestBuilder, imageView, requestOptions)
    }

    fun loadFromNetworkAsBitmap(context: Context, url: String): Bitmap {
        return Glide.with(context).asBitmap().load(url).submit().get()
    }

    private fun loadImage(requestBuilder: RequestBuilder<Drawable>, imageView: ImageView, requestOptions: RequestOptions? = null) {
        requestBuilder
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                //.apply(placeholderOf(R.mipmap.ic_empty_cover))
                .apply(errorOf(R.mipmap.ic_empty_cover))
        if (requestOptions != null) {
            requestBuilder.apply(requestOptions)
        }
        requestBuilder.into(imageView)
    }
}