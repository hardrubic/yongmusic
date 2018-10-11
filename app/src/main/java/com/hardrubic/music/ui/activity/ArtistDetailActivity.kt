package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import com.hardrubic.music.entity.vo.AlbumVO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.ui.adapter.MyViewPagerAdapter
import com.hardrubic.music.ui.fragment.ArtistAlbumFragment
import com.hardrubic.music.ui.fragment.ArtistDescFragment
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import com.hardrubic.music.util.LoadImageUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_artist_detail.*
import java.io.Serializable
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class ArtistDetailActivity : BaseActivity() {

    private val artistId: Long by lazy {
        intent.getLongExtra(Constant.Param.ARTIST_ID, -1)
    }

    private val viewModel: ArtistDetailViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ArtistDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_detail)

        initView()
        applyQueryData()
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.artist)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()
    }

    private fun refreshArtistDetail(artist: NeteaseArtistDetail, musics: List<MusicVO>, albums: List<AlbumVO>) {
        collapsing_toolbar_layout.title = artist.name
        LoadImageUtil.loadFromNetwork(this, artist.picUrl, iv_cover)

        val artistAlbumFragmentArg = Bundle().apply {
            putSerializable(Constant.Param.LIST, albums as Serializable)
        }
        val artistDescFragmentArg = Bundle().apply {
            putString(Constant.Param.NAME, artist.briefDesc)
        }

        val artistAlbumFragment = ArtistAlbumFragment().apply {
            arguments = artistAlbumFragmentArg
        }
        val artistDescFragment = ArtistDescFragment().apply {
            arguments = artistDescFragmentArg
        }

        vp_list.adapter = MyViewPagerAdapter(supportFragmentManager).apply {
            addFragment(CommonMusicListFragment.instance(musics), getString(R.string.music))
            addFragment(artistAlbumFragment, getString(R.string.album))
            addFragment(artistDescFragment, getString(R.string.introduction))
        }
        tab.tabMode = TabLayout.MODE_FIXED
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
        tab.setupWithViewPager(vp_list)
    }

    private fun applyQueryData() {
        applyQueryArtistDetail()
        applyQueryArtistAlbum()

        thread {
            val countDownLatch = CountDownLatch(3)

            viewModel.artistDetailData.observe(this, Observer {
                countDownLatch.countDown()
            })
            viewModel.artistHotMusicData.observe(this, Observer {
                countDownLatch.countDown()
            })
            viewModel.artistHotAlbumData.observe(this, Observer {
                countDownLatch.countDown()
            })

            countDownLatch.await()

            runOnUiThread {
                refreshArtistDetail(viewModel.artistDetailData.value!!,
                        viewModel.artistHotMusicData.value!!,
                        viewModel.artistHotAlbumData.value!!)
            }
        }
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

    private fun applyQueryArtistAlbum() {
        viewModel.internalQueryArtistHotAlbum(artistId, Consumer { throwable ->
            ShowExceptionHelper.show(this, throwable, object : DialogBtnListener {
                override fun onClickOkListener(dialog: DialogInterface?) {
                    applyQueryArtistAlbum()
                    dialog?.dismiss()
                }

                override fun onClickCancelListener(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }
            })
            throwable.printStackTrace()
        })
    }

    companion object {
        fun start(context: Context, artistId: Long) {
            context.startActivity(Intent(context, ArtistDetailActivity::class.java).apply {
                putExtra(Constant.Param.ARTIST_ID, artistId)
            })
        }
    }
}
