package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.biz.command.ApplyCurrentMusicCommand
import com.hardrubic.music.biz.command.ApplyPlayStateCommand
import com.hardrubic.music.biz.command.PlayOrPauseCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.listener.MusicStateListener
import com.hardrubic.music.biz.vm.MusicControlViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.activity.BaseActivity
import com.hardrubic.music.ui.activity.PlayingActivity
import kotlinx.android.synthetic.main.fragment_music_control.*


class MusicControlFragment : BaseFragment(), MusicStateListener {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MusicControlViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (mActivity as BaseActivity).addMusicStateListener(this)
        return inflater.inflate(R.layout.fragment_music_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    override fun onDestroy() {
        (mActivity as BaseActivity).removeMusicStateListener(this)
        super.onDestroy()
    }

    private fun initData() {
    }

    private fun initView() {
        ll_info.setOnClickListener {
            if (checkPlaying()) {
                startActivity(Intent(activity, PlayingActivity::class.java))
            }
        }
        iv_play.setOnClickListener {
            RemoteControl.executeCommand(PlayOrPauseCommand())
        }
        iv_list.setOnClickListener {
            val fragment = PlayListFragment()
            fragment.show(mActivity.supportFragmentManager, PlayListFragment.TAG)
        }

        RemoteControl.executeCommand(ApplyCurrentMusicCommand())
        RemoteControl.executeCommand(ApplyPlayStateCommand())
    }

    private fun checkPlaying(): Boolean {
        val musicId = CurrentPlayingHelper.getPlayingMusicId()
        return musicId != null
    }

    override fun updateProgress(progress: Int) {
        if (progress < 0) {
            pb_progress.progress = 0
        } else {
            pb_progress.progress = progress
        }
    }

    override fun updateCurrentMusic(music: Music) {
        tv_music_name.text = music.name
        tv_artist.text = music.artist
        pb_progress.max = music.duration
    }

    override fun updatePlayingState(flag: Boolean) {
        if (flag) {
            iv_play.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            iv_play.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MusicControlFragment().apply {
            arguments = Bundle()
        }
    }
}