package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.LocalMusicViewModel
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import kotlinx.android.synthetic.main.activity_local_music.*

class LocalMusicActivity : BaseActivity() {

    private val viewModel: LocalMusicViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(LocalMusicViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_music)

        initData()
        initView()

        viewModel.searchLocalMusic()
    }

    private fun initData() {
        viewModel.localMusicData.observe(this, Observer<List<MusicVO>> { it ->
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_music_list, CommonMusicListFragment.instance(it!!, network = false))
                    .commit()
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.local_music)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()
    }
}
