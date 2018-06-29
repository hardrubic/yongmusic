package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.util.LoadImageUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_artist_detail.*

class ArtistDetailActivity : BaseActivity() {

    private val artistId by lazy {
        intent.getLongExtra(Constant.Param.ARTIST_ID, -1)
    }

    private val viewModel: ArtistDetailViewModel by lazy {
        ViewModelProviders.of(this).get(ArtistDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_detail)

        initData()
        initView()
        applyQueryArtistDetail()
    }

    private fun initData() {
        viewModel.artistDetailData.observe(this, Observer {
            it?.let {
                refreshArtistDetail(it)
            }
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.artist)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()
    }

    private fun refreshArtistDetail(artistDetail: NeteaseArtistDetail) {
        supportActionBar!!.title = artistDetail.name
        LoadImageUtil.loadFromNetwork(this, artistDetail.picUrl, iv_cover)
    }

    private fun applyQueryArtistDetail() {
        viewModel.internalQueryArtistDetail(artistId, Consumer { throwable ->
            ShowExceptionHelper.show(this, throwable, object : DialogBtnListener {
                override fun onClickOkListener(dialog: DialogInterface?) {
                    applyQueryArtistDetail()
                    dialog?.dismiss()
                }

                override fun onClickCancelListener(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }
            })
            throwable.printStackTrace()
        })
    }
}
