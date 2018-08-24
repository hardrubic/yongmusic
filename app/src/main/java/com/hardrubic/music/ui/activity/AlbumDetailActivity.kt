package com.hardrubic.music.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.AlbumDetailResponse
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import com.hardrubic.music.ui.widget.statusbar.StatusBarColor
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_album_detail.*
import kotlinx.android.synthetic.main.layout_album_detail_cover.*

class AlbumDetailActivity : BaseActivity() {

    private val albumId: Long by lazy {
        intent.getLongExtra(Constant.Param.ALBUM_ID, -1)
    }
    private var albumName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        initData()
        initView()
        applyQueryAlbumDetail()
    }

    private fun initData() {
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.album)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        StatusBarColor.setStatusBarLightMode(this, ContextCompat.getColor(this, R.color.black))

        showMusicControl()

        app_bar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == toolbar.height - collapsing_toolbar_layout.height) {
                collapsing_toolbar_layout.title = albumName
            } else {
                collapsing_toolbar_layout.title = ""
            }
        }
    }

    private fun refreshAlbumDetail(albumDetail: AlbumDetailResponse) {
        val album = albumDetail.album!!
        this.albumName = album.name

        LoadImageUtil.loadFromNetwork(this, album.picUrl, iv_cover)
        tv_name.text = album.name
        tv_artist.text = FormatUtil.formatArtistNames(album.getArtistNames())
        tv_time.text = FormatUtil.formatPublishTime(album.publishTime)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_music_list, CommonMusicListFragment.instance(albumDetail.getMusicVOs()))
                .commit()
    }

    //TODO move to viewmodel
    private fun applyQueryAlbumDetail() {
        HttpService.instance.applyAlbumDetail(albumId)
                .subscribe(Consumer {
                    refreshAlbumDetail(it)
                }, Consumer {
                    ShowExceptionHelper.show(this, it, object : DialogBtnListener {
                        override fun onClickOkListener(dialog: DialogInterface?) {
                            applyQueryAlbumDetail()
                            dialog?.dismiss()
                        }

                        override fun onClickCancelListener(dialog: DialogInterface?) {
                            dialog?.dismiss()
                        }
                    })
                    it.printStackTrace()
                })
    }

    companion object {
        fun start(context: Context, albumId: Long) {
            context.startActivity(Intent(context, AlbumDetailActivity::class.java).apply {
                putExtra(Constant.Param.ALBUM_ID, albumId)
            })
        }
    }
}
