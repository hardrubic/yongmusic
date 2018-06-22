package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.LocalMusicViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.adapter.MusicListAdapter
import com.hardrubic.music.ui.widget.view.MusicBatchHeaderView
import com.hardrubic.music.ui.widget.view.OnMusicBatchHeaderViewListener
import kotlinx.android.synthetic.main.activity_local_music.*
import java.util.*

class LocalMusicActivity : BaseActivity() {

    private lateinit var headView: MusicBatchHeaderView
    private lateinit var adapter: MusicListAdapter
    private val viewModel: LocalMusicViewModel by lazy {
        ViewModelProviders.of(this).get(LocalMusicViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_music)

        initData()
        initView()

        viewModel.searchLocalMusic()
    }

    private fun initData() {
        viewModel.localMusicData.observe(this, Observer<List<Music>> { musics ->
            val num = musics!!.size
            adapter.setNewData(musics)
            headView.refreshNum(num)
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.local_music)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        headView = MusicBatchHeaderView(this)
        headView.listener = object : OnMusicBatchHeaderViewListener {
            override fun selectAll() {
                viewModel.selectMusic(adapter.data)
            }
        }

        adapter = MusicListAdapter(Collections.emptyList())
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            adapter as MusicListAdapter
            val music = adapter.getItem(position)!!

            viewModel.selectMusic(listOf(music))
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
