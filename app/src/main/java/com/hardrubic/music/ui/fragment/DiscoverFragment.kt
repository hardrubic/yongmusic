package com.hardrubic.music.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hardrubic.music.R
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onStart() {
        super.onStart()
        banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        banner.stopAutoPlay()
    }

    private fun initView() {
        val imageUrls = resources.getStringArray(R.array.url).toList()

        banner.setImageLoader(BannerImageLoader())
        banner.setOnBannerListener {
            Snackbar.make(banner, "position:$it", Snackbar.LENGTH_LONG).show()
        }
        banner.setImages(imageUrls)
        banner.start()
    }

    private class BannerImageLoader : ImageLoader() {
        override fun displayImage(context: Context, path: Any, imageView: ImageView) {
            Glide.with(context).load(path).into(imageView)
        }

    }
}