package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.MusicHelper
import com.hardrubic.music.biz.helper.ShowExceptionHelper
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.biz.resource.MusicResourceDownload
import com.hardrubic.music.biz.vm.CommonMusicListViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.adapter.show.ShowMusicAdapter
import com.hardrubic.music.ui.widget.view.MusicBatchHeaderView
import com.hardrubic.music.ui.widget.view.OnMusicBatchHeaderViewListener
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.io.Serializable
import java.util.*

class CommonMusicListFragment : BaseFragment() {

    private val musics: List<MusicVO>
        get() = arguments?.getSerializable(Constant.Param.LIST) as List<MusicVO>? ?: Collections.emptyList()
    private val network: Boolean
        get() = arguments?.getBoolean(Constant.Param.NETWORK) as Boolean

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
        val headView = MusicBatchHeaderView(mActivity)
        headView.refreshNum(musics.size)
        headView.listener = object : OnMusicBatchHeaderViewListener {
            override fun selectAll() {
                val adapter = rv_list.adapter as ShowMusicAdapter
                val musicIds = adapter.data.map { it.musicId }
                if (musicIds.isEmpty()) {
                    return
                }

                applySelectMusic(musicIds) { it ->
                    viewModel.playMusics(it)
                }
            }
        }

        val adapter = ShowMusicAdapter(musics)
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            val musicVO = (adapter as ShowMusicAdapter).getItem(position)!!

            applySelectMusic(listOf(musicVO.musicId)) {
                viewModel.playMusic(musicVO.musicId)
            }
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun applySelectMusic(initialMusicIds: List<Long>, successCallback: (musics: List<Music>) -> Unit) {
        if (network) {
            val progressDialogFragment = ProgressDialogFragment()
            progressDialogFragment.show(mActivity.supportFragmentManager.beginTransaction(), ProgressDialogFragment.TAG)

            MusicResourceDownload.downloadMusicResource(mActivity, initialMusicIds, object : MusicResourceListener {
                override fun onProgress(progress: Int, max: Int) {
                    progressDialogFragment.refreshProgress(progress, max)
                }

                override fun onSuccess(musics: List<Music>) {
                    viewModel.saveMusics(musics)
                    successCallback.invoke(MusicHelper.sortMusicByInitialId(musics, initialMusicIds))
                    progressDialogFragment.dismiss()
                }

                override fun onError(e: Throwable) {
                    ShowExceptionHelper.show(mActivity, e)
                }
            })
        } else {
            val musics = viewModel.queryMusics(initialMusicIds)
            successCallback.invoke(MusicHelper.sortMusicByInitialId(musics, initialMusicIds))
        }
    }

    companion object {

        fun instance(musicVOs: List<MusicVO>, network: Boolean = true): CommonMusicListFragment {
            val bundle = Bundle().apply {
                if (musicVOs.isNotEmpty()) {
                    putSerializable(Constant.Param.LIST, musicVOs as Serializable)
                }
                putBoolean(Constant.Param.NETWORK, network)
            }
            return CommonMusicListFragment().apply {
                arguments = bundle
            }
        }
    }
}