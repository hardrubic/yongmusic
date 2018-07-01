package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import com.hardrubic.music.network.response.ArtistHotMusicResponse
import com.hardrubic.music.network.response.entity.NeteaseArtistDetail
import com.hardrubic.music.ui.adapter.MyViewPagerAdapter
import com.hardrubic.music.ui.fragment.ArtistDescFragment
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import com.hardrubic.music.ui.fragment.search.SearchMusicListFragment
import com.hardrubic.music.util.LoadImageUtil
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_artist_detail.*
import java.io.Serializable
import java.util.*

class ArtistDetailActivity : BaseActivity() {

    private val artistId: Long by lazy {
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

    private fun refreshArtistDetail(artistDetail: ArtistHotMusicResponse) {
        val artist = artistDetail.artist!!

        collapsing_toolbar_layout.title = artist.name
        LoadImageUtil.loadFromNetwork(this, artist.picUrl, iv_cover)

        val commonMusicListFragmentArg = Bundle().apply {
            putSerializable(Constant.Param.LIST, artistDetail.getMusicVOs() as Serializable)
        }
        val commonMusicListFragment = CommonMusicListFragment().apply {
            arguments = commonMusicListFragmentArg
        }

        val artistDescFragmentArg = Bundle().apply {
            putString(Constant.Param.NAME, artist.briefDesc)
        }
        val artistDescFragment = ArtistDescFragment().apply {
            arguments = artistDescFragmentArg
        }

        vp_list.adapter = MyViewPagerAdapter(supportFragmentManager).apply {
            addFragment(commonMusicListFragment, getString(R.string.music))
            addFragment(artistDescFragment, getString(R.string.album))
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
