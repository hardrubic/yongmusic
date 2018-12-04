package com.hardrubic.music.ui.fragment.search

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.interf.Searchable
import com.hardrubic.music.biz.search.SearchErrorAction
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.ui.activity.ArtistDetailActivity
import com.hardrubic.music.ui.adapter.ArtistListAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class SearchArtistListFragment : BaseFragment(), Searchable {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(mActivity, viewModelFactory).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: ArtistListAdapter
    private var currentSearchText = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_result_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initView()
    }

    private fun initView() {
        adapter = ArtistListAdapter(Collections.emptyList())
        adapter.setOnItemClickListener { adapter, view, position ->
            val artist = (adapter as ArtistListAdapter).getItem(position)!!
            ArtistDetailActivity.start(mActivity, artist.artistId)
        }
        adapter.setOnLoadMoreListener({
            viewModel.searchMoreArtist(currentSearchText, SearchErrorAction(mActivity))
        }, rv_list)
        adapter.emptyView = LayoutInflater.from(activity).inflate(R.layout.layout_empty_list_hint, null)

        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL))

        swipe_refresh_layout.setOnRefreshListener {
            search(currentSearchText)
        }
    }

    private fun initData() {
        viewModel.artistData.observe(this, android.arch.lifecycle.Observer {
            adapter.setNewData(it)
            swipe_refresh_layout.isRefreshing = false
        })
        viewModel.searchMoreEnd.observe(this, android.arch.lifecycle.Observer {
            swipe_refresh_layout.isRefreshing = false
            adapter.loadMoreEnd()
        })
    }

    override fun search(text: String) {
        swipe_refresh_layout.isRefreshing = true
        currentSearchText = text
        viewModel.searchArtist(text, SearchErrorAction(mActivity))
    }
}