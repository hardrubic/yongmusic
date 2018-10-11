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
import com.hardrubic.music.ui.activity.AlbumDetailActivity
import com.hardrubic.music.ui.adapter.show.ShowAlbumAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import com.hardrubic.music.util.LoadingDialogUtil
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class SearchAlbumListFragment : BaseFragment(), Searchable {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(mActivity, viewModelFactory).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: ShowAlbumAdapter
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
        adapter = ShowAlbumAdapter(Collections.emptyList())
        adapter.setOnItemClickListener { adapter, view, position ->
            val album = (adapter as ShowAlbumAdapter).getItem(position)!!
            AlbumDetailActivity.start(mActivity, album.albumId)
        }
        adapter.setOnLoadMoreListener({
            viewModel.searchMoreAlbum(currentSearchText, SearchErrorAction(mActivity))
        }, rv_list)
        adapter.emptyView = LayoutInflater.from(activity).inflate(R.layout.layout_empty_list_hint, null)

        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL))
    }

    private fun initData() {
        viewModel.albumData.observe(this, android.arch.lifecycle.Observer {
            adapter.setNewData(it)
            LoadingDialogUtil.getInstance().dismissLoadingDialog()
        })
        viewModel.searchMoreEnd.observe(this, android.arch.lifecycle.Observer {
            adapter.loadMoreEnd()
        })
    }

    override fun search(text: String) {
        currentSearchText = text
        viewModel.searchAlbum(text, SearchErrorAction(mActivity))
    }
}