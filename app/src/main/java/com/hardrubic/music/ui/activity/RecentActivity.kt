package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.RecentViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.adapter.MusicListAdapter
import kotlinx.android.synthetic.main.activity_recent.*
import java.util.*

class RecentActivity : BaseActivity() {

    private val viewModel: RecentViewModel by lazy {
        ViewModelProviders.of(this).get(RecentViewModel::class.java)
    }
    private lateinit var musicAdapter: MusicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent)

        initData()
        initView()
        viewModel.recentMusicList()
    }

    private fun initData() {
        viewModel.recentData.observe(this, android.arch.lifecycle.Observer<List<Music>> { musics ->
            musicAdapter.setNewData(musics)
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.recent_play)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        musicAdapter = MusicListAdapter(Collections.emptyList())
        musicAdapter.setOnItemClickListener { adapter, view, position ->
            val music = (adapter as MusicListAdapter).getItem(position)!!
            viewModel.selectRecentMusic(music)

        }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = musicAdapter
    }
}
