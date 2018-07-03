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
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.biz.vm.CommonMusicListViewModel
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.adapter.show.ShowMusicAdapter
import com.hardrubic.music.ui.widget.view.MusicBatchHeaderView
import com.hardrubic.music.ui.widget.view.OnMusicBatchHeaderViewListener
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_search_result_list.*

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
        val headView = MusicBatchHeaderView(mActivity)
        headView.refreshNum(musics.size)
        headView.listener = object : OnMusicBatchHeaderViewListener {
            override fun selectAll() {
                val adapter = rv_list.adapter as ShowMusicAdapter
                val musicIds = adapter.data.map { it.musicId }
                if (musicIds.isEmpty()) {
                    return
                }

                viewModel.selectMusic(musicIds, musicIds.first(), Consumer {
                    showError(it)
                })
            }
        }

        val adapter = ShowMusicAdapter(musics)
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            val musicVO = (adapter as ShowMusicAdapter).getItem(position)!!

            viewModel.selectMusic(musicVO.musicId, Consumer {
                showError(it)
            })
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun showError(throwable: Throwable) {
        ShowExceptionHelper.show(mActivity, throwable, object : DialogBtnListener {
            override fun onClickOkListener(dialog: DialogInterface?) {
                //TODO 没有确定按钮
                dialog?.dismiss()
            }

            override fun onClickCancelListener(dialog: DialogInterface?) {
                dialog?.dismiss()
            }
        })
        throwable.printStackTrace()
    }
}