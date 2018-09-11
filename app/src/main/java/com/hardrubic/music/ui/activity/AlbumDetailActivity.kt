package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.biz.vm.AlbumDetailViewModel
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

    private val viewModel: AlbumDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AlbumDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        initData()
        initView()

        viewModel.internalQueryAlbumDetail(albumId, Consumer {
            ShowExceptionHelper.show(this, it, object : DialogBtnListener {
                override fun onClickOkListener(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }

                override fun onClickCancelListener(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }
            })
            it.printStackTrace()
        })
    }

    private fun initData() {
        viewModel.albumDetailData.observe(this, Observer {
            this.albumName = it!!.name

            LoadImageUtil.loadFromNetwork(this, it.picUrl, iv_cover)
            tv_name.text = it.name
            tv_artist.text = FormatUtil.formatArtistNames(it.getArtistNames())
            tv_time.text = FormatUtil.formatPublishTime(it.publishTime)
        })
        viewModel.albumMusicData.observe(this, Observer {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_music_list, CommonMusicListFragment.instance(it!!))
                    .commit()
        })
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

    companion object {
        fun start(context: Context, albumId: Long) {
            context.startActivity(Intent(context, AlbumDetailActivity::class.java).apply {
                putExtra(Constant.Param.ALBUM_ID, albumId)
            })
        }
    }
}
