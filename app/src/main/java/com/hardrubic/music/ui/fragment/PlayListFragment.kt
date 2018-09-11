package com.hardrubic.music.ui.fragment

import android.app.Dialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.command.LoopCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.StopCommand
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.biz.vm.PlayListViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.ui.adapter.PlayListAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_play_list.*
import java.util.*
import javax.inject.Inject

class PlayListFragment : AppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val adapter = PlayListAdapter(Collections.emptyList())
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PlayListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_play_list, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        viewModel.queryPlayList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(this.context!!, this.theme)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    private fun initData() {
        viewModel.playListData.observe(this, android.arch.lifecycle.Observer { musics ->
            refreshList(musics!!)
        })
    }

    private fun initView() {
        iv_play_model.setOnClickListener {
            changePlayModel()
        }
        tv_play_model.setOnClickListener {
            changePlayModel()
        }
        loadPlayModelNameAndIcon(PlayModelHelper.loadPlayModel())

        iv_delete_all.setOnClickListener {
            deleteAllMusic()
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            adapter as PlayListAdapter
            val music = adapter.getItem(position)!!
            selectMusic(music)
        }
        adapter.setOnItemChildClickListener { adapter, view, position ->
            adapter as PlayListAdapter
            val music = adapter.getItem(position)!!

            when (view.id) {
                R.id.iv_delete -> deleteMusic(music, position)
            }
        }
        adapter.playingMusicId = CurrentPlayingHelper.getPlayingMusicId()
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = adapter
        rv_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun refreshList(list: List<Music>) {
        adapter.setNewData(list)
    }

    private fun selectMusic(music: Music) {
        adapter.playingMusicId = music.musicId
        adapter.notifyDataSetChanged()

        viewModel.selectMusic(music)
    }

    private fun deleteMusic(music: Music, position: Int) {
        PlayListHelper.delete(music)

        if (adapter.itemCount == 1) {
            deleteAllMusic()
        } else {
            var playing = false
            MusicServiceControl.runInMusicService(activity!!, {
                playing = it.isPlaying()
            })
            if (adapter.playingMusicId == music.musicId && playing) {
                val nextMusicPosition = if (position == adapter.data.lastIndex) {
                    0
                } else {
                    position + 1
                }
                val nextMusic = adapter.getItem(nextMusicPosition)!!
                selectMusic(nextMusic)
            }
            adapter.remove(position)
        }
    }

    private fun deleteAllMusic() {
        adapter.playingMusicId = null
        adapter.setNewData(Collections.emptyList())
        PlayListHelper.deleteAll()
        CurrentPlayingHelper.setPlayingMusicId(null)

        MusicServiceControl.runInMusicService(activity!!) {
            RemoteControl.executeCommand(StopCommand(it))
        }
    }

    private fun changePlayModel() {
        val currentPlayModel = PlayModelHelper.loadPlayModel()
        val nextPlayModel = PlayModelHelper.nextPlayModel(currentPlayModel)

        MusicServiceControl.runInMusicService(activity!!) {
            RemoteControl.executeCommand(LoopCommand(nextPlayModel, it))
        }

        loadPlayModelNameAndIcon(nextPlayModel)
    }

    private fun loadPlayModelNameAndIcon(model: Int) {
        val stringId = PlayModelHelper.getPlayModelStringId(model)
        val iconId = PlayModelHelper.getPlayModelIconId(model)
        tv_play_model.text = getString(stringId)
        iv_play_model.setImageDrawable(resources.getDrawable(iconId))
    }

    companion object {
        public val TAG = PlayListFragment::class.java.simpleName
    }
}