package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.SearchView
import com.hardrubic.music.R
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.adapter.MusicListAdapter
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class SearchActivity : BaseActivity() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }
    private lateinit var musicAdapter: MusicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initData()
        initView()
    }

    private fun initData() {
        viewModel.searchData.observe(this, android.arch.lifecycle.Observer<List<Music>> { musics ->
            musicAdapter.setNewData(musics)
        })
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showMusicControl()

        musicAdapter = MusicListAdapter(Collections.emptyList())
        musicAdapter.setOnItemClickListener { adapter, view, position ->
            val music = (adapter as MusicListAdapter).getItem(position)!!

            viewModel.applyCacheAndPlayMusic(music)
        }
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = musicAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_action, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchItem.expandActionView()

        val searchView = searchItem.actionView as SearchView
        //searchView.queryHint = resources.getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchMusic(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //mPresenter.searchTask(newText.toUpperCase())
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
