package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.RecentViewModel
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import kotlinx.android.synthetic.main.activity_recent.*

class RecentActivity : BaseActivity() {

    private val viewModel: RecentViewModel by lazy {
        ViewModelProviders.of(this).get(RecentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent)

        initData()
        initView()
        viewModel.recentMusicList()
    }

    private fun initData() {
        viewModel.recentData.observe(this, android.arch.lifecycle.Observer<List<MusicVO>> {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_music_list, CommonMusicListFragment.instance(it!!, network = false))
                    .commit()
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.recent_play)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()
    }
}
