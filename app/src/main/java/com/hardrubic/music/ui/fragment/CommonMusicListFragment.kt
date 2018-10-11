package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.helper.MusicHelper
import com.hardrubic.music.biz.interf.MusicResourceListener
import com.hardrubic.music.biz.resource.MusicResourceDownload
import com.hardrubic.music.biz.vm.CommonMusicListViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.ui.adapter.show.ShowMusicAdapter
import com.hardrubic.music.ui.widget.view.MusicBatchHeaderView
import com.hardrubic.music.ui.widget.view.OnMusicBatchHeaderViewListener
import dagger.Lazy
import kotlinx.android.synthetic.main.fragment_search_result_list.*
import java.io.Serializable
import java.util.*
import javax.inject.Inject

class CommonMusicListFragment : BaseFragment() {

    private val musics: List<MusicVO>
        get() = arguments?.getSerializable(Constant.Param.LIST) as List<MusicVO>? ?: Collections.emptyList()
    private val network: Boolean
        get() = arguments?.getBoolean(Constant.Param.NETWORK) as Boolean

    @Inject
    lateinit var musicResourceDownload: Lazy<MusicResourceDownload>

    private var exceptionDialogFragment: ExceptionDialogFragment? = null

    private val viewModel: CommonMusicListViewModel by lazy {
        ViewModelProviders.of(mActivity, viewModelFactory).get(CommonMusicListViewModel::class.java)
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
                val validMusicIds = adapter.data.filter { it.valid }.map { it.musicId }
                if (validMusicIds.isEmpty()) {
                    return
                }

                applySelectMusic(validMusicIds) { it ->
                    viewModel.playMusics(it)
                }
            }
        }

        val adapter = ShowMusicAdapter(musics)
        adapter.addHeaderView(headView)
        adapter.setOnItemClickListener { adapter, view, position ->
            val musicVO = (adapter as ShowMusicAdapter).getItem(position)!!
            if (!musicVO.valid) {
                Toast.makeText(mActivity, R.string.hint_music_invalid, Toast.LENGTH_LONG).show()
                return@setOnItemClickListener
            }

            applySelectMusic(listOf(musicVO.musicId)) {
                viewModel.playMusic(musicVO.musicId)
            }
        }
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun applySelectMusic(musicIds: List<Long>, successCallback: (musics: List<Music>) -> Unit) {
        if (network) {
            val progressDialogFragment = ProgressDialogFragment()
            progressDialogFragment.show(mActivity.supportFragmentManager.beginTransaction(), ProgressDialogFragment.TAG)

            musicResourceDownload.get().downloadMusicResource(mActivity, musicIds, object : MusicResourceListener {
                override fun onSuccess(musicRelatedBOs: List<MusicRelatedBO>) {
                    viewModel.saveMusicRelated(musicRelatedBOs)
                    val musics = musicRelatedBOs.map { it.music }
                    successCallback.invoke(MusicHelper.sortMusicByTargetId(musics, musicIds))
                    progressDialogFragment.dismiss()
                }

                override fun onProgress(progress: Int, max: Int) {
                    progressDialogFragment.refreshProgress(progress, max)
                }

                override fun onError(e: Throwable) {
                    progressDialogFragment.dismiss()

                    //TODO test
                    if (musicIds.size == 1) {
                        showErrorDialog(e)
                    } else {
                        e.printStackTrace()
                    }
                }
            })
        } else {
            val musics = viewModel.queryMusics(musicIds)
            successCallback.invoke(MusicHelper.sortMusicByTargetId(musics, musicIds))
        }
    }

    @Synchronized
    private fun showErrorDialog(e: Throwable) {
        //TODO 待统一弹窗
        if (exceptionDialogFragment != null && exceptionDialogFragment!!.isAdded) {
            return
        }
        exceptionDialogFragment = ExceptionDialogFragment(e, null)
        exceptionDialogFragment?.showNow(mActivity.supportFragmentManager, ExceptionDialogFragment.TAG)
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