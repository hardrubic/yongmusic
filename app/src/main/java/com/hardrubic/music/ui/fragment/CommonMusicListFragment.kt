package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.biz.listener.SearchRefreshListener
import com.hardrubic.music.biz.vm.CommonMusicListViewModel
import com.hardrubic.music.biz.vm.SearchViewModel
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.adapter.MusicListAdapter
import com.hardrubic.music.ui.adapter.ShowMusicAdapter
import com.hardrubic.music.ui.fragment.BaseFragment
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.util.*

class CommonMusicListFragment : BaseFragment() {

    private val musics: List<MusicVO>
        get() = arguments?.getSerializable(Constant.Param.LIST) as List<MusicVO>

    private val viewModel: CommonMusicListViewModel by lazy {
        ViewModelProviders.of(mActivity).get(CommonMusicListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_common_music_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        val adapter = ShowMusicAdapter(musics)
        adapter.setOnItemClickListener { adapter, view, position ->
            val musicVO = (adapter as ShowMusicAdapter).getItem(position)!!

            viewModel.applyCacheAndPlayMusic(musicVO.musicId)
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }
}