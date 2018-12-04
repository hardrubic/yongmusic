package com.hardrubic.music.ui.fragment.search

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.biz.interf.Searchable
import com.hardrubic.music.biz.resource.MusicResourceDownload
import com.hardrubic.music.biz.search.SearchErrorAction
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.adapter.show.ShowMusicAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import com.hardrubic.music.ui.fragment.ProgressDialogFragment
import dagger.Lazy
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*
import javax.inject.Inject

class SearchMusicListFragment : BaseFragment(), Searchable {

    @Inject
    lateinit var musicResourceDownload: Lazy<MusicResourceDownload>

    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(mActivity, viewModelFactory).get(SearchViewModel::class.java)
    }

    private lateinit var adapter: ShowMusicAdapter
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
        adapter = ShowMusicAdapter(Collections.emptyList())
        adapter.setOnItemClickListener { adapter, view, position ->
            val musicVO = (adapter as ShowMusicAdapter).getItem(position)!!
            applySelectMusic(musicVO)
        }
        adapter.setOnLoadMoreListener({
            viewModel.searchMoreMusic(currentSearchText, SearchErrorAction(mActivity))
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
        viewModel.musicData.observe(this, android.arch.lifecycle.Observer {
            adapter.setNewData(it)
            swipe_refresh_layout.isRefreshing = false
        })
        viewModel.searchMoreEnd.observe(this, android.arch.lifecycle.Observer {
            adapter.loadMoreEnd()
            swipe_refresh_layout.isRefreshing = false
        })
    }

    private fun applySelectMusic(musicVO: MusicVO) {
        val progressDialogFragment = ProgressDialogFragment()
        progressDialogFragment.show(mActivity.supportFragmentManager.beginTransaction(), ProgressDialogFragment.TAG)

        musicResourceDownload.get().downloadMusicResource(mActivity, listOf(musicVO.musicId), object : MusicResourceListener {
            override fun onSuccess(musicRelatedBOs: List<MusicRelatedBO>) {
                val musics = musicRelatedBOs.map { it.music!! }
                viewModel.saveMusicRelated(musicRelatedBOs)
                viewModel.playMusics(musics, musics.first().musicId)
                progressDialogFragment.dismiss()
            }

            override fun onProgress(progress: Int, max: Int) {
                progressDialogFragment.refreshProgress(progress, max)
            }

            override fun onError(e: Throwable) {
                progressDialogFragment.dismiss()
                ShowExceptionHelper.show(mActivity, e)
            }
        })
    }

    override fun search(text: String) {
        swipe_refresh_layout.isRefreshing = true
        currentSearchText = text
        viewModel.searchMusic(text, SearchErrorAction(mActivity))
    }
}