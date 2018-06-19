package com.hardrubic.music.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.LocalMusicViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.adapter.MusicListAdapter
import com.hardrubic.music.util.LoadingDialogHelper
import kotlinx.android.synthetic.main.activity_local_music.*
import java.util.*

class LocalMusicActivity : BaseActivity() {

    private lateinit var headView: View
    private lateinit var adapter: MusicListAdapter
    private val viewModel: LocalMusicViewModel by lazy {
        ViewModelProviders.of(this).get(LocalMusicViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_music)

        initData()
        initView()

        LoadingDialogHelper.instance.showDefaultLoadingDialog(this)
        viewModel.searchLocalMusic()
    }

    private fun initData() {
        viewModel.localMusicData.observe(this, Observer<List<Music>> { musics ->
            val num = musics!!.size
            adapter.setNewData(musics)
            headView.findViewById<TextView>(R.id.tv_music_num).text = getString(R.string.music_num, num)
            LoadingDialogHelper.instance.dismissLoadingDialog()
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.local_music)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        //todo 封装headerView为控件
        headView = layoutInflater.inflate(R.layout.layout_music_list_head, null)
        headView.findViewById<LinearLayout>(R.id.ll_main).setOnClickListener {
            viewModel.selectLocalMusic(adapter.data)
        }

        adapter = MusicListAdapter(Collections.emptyList())
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            adapter as MusicListAdapter
            val music = adapter.getItem(position)!!

            viewModel.selectLocalMusic(listOf(music))
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
