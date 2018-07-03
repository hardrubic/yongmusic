package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.CollectionDetailViewModel
import com.hardrubic.music.ui.adapter.show.ShowMusicAdapter
import com.hardrubic.music.ui.widget.view.MusicBatchHeaderView
import com.hardrubic.music.ui.widget.view.OnMusicBatchHeaderViewListener
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : BaseActivity() {

    private val collection by lazy {
        val id = intent.getLongExtra(Constant.Param.COLLECTION_ID, -1)
        viewModel.queryCollection(id)!!
    }

    private lateinit var adapter: ShowMusicAdapter
    private val viewModel: CollectionDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CollectionDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_collection_detail_action, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_edit) {
            Snackbar.make(rv_list, "todo", Snackbar.LENGTH_SHORT).show()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        setSupportActionBar(toolbar.apply {
            this.title = getString(R.string.collection)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        tv_name.text = collection.name

        val musicVOs = viewModel.queryCollectionMusicVOs(collection.id)

        val headView = MusicBatchHeaderView(this)
        headView.refreshNum(musicVOs.size)
        headView.listener = object : OnMusicBatchHeaderViewListener {
            override fun selectAll() {
                viewModel.selectMusic(adapter.data.map { it.musicId })
            }
        }

        adapter = ShowMusicAdapter(musicVOs)
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            adapter as ShowMusicAdapter
            val vos = adapter.data
            val musicIds = vos.map { it.musicId }
            val playMusicId = adapter.getItem(position)!!.musicId

            viewModel.selectMusic(musicIds, playMusicId)
        }

        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
