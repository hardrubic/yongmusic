package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.R
import com.hardrubic.music.biz.command.*
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.vm.MusicControlViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.ui.activity.PlayingActivity
import com.hardrubic.music.util.DrawableUtil
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil
import kotlinx.android.synthetic.main.fragment_music_control.*


class MusicControlFragment : BaseFragment() {
    private val musicServiceControl = MusicServiceControl()

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MusicControlViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onStart() {
        super.onStart()
        musicServiceControl.register(mActivity, musicBroadcastListener) {
            RemoteControl.executeCommand(ApplyCurrentMusicCommand(musicServiceControl))
            RemoteControl.executeCommand(ApplyPlayStateCommand(musicServiceControl))
        }
    }

    override fun onStop() {
        super.onStop()
        musicServiceControl.unregister(mActivity)
    }

    private fun initView() {
        ll_info.setOnClickListener {
            if (checkPlaying()) {
                startActivity(Intent(activity, PlayingActivity::class.java))
            }
        }
        iv_play.setOnClickListener {
            if (musicServiceControl.isPlaying()) {
                RemoteControl.executeCommand(PauseCommand(musicServiceControl))
            } else {
                RemoteControl.executeCommand(PlayCommand(musicServiceControl))
            }
        }
        iv_list.setOnClickListener {
            val fragment = PlayListFragment()
            fragment.show(mActivity.supportFragmentManager, PlayListFragment.TAG)
        }
        DrawableUtil.setImageViewColor(iv_list, R.color.second_text_color)
    }

    private fun checkPlaying(): Boolean {
        val musicId = CurrentPlayingHelper.getPlayingMusicId()
        return musicId != null
    }

    private fun updateCover(music: Music) {
        val albumId = music.albumId
        if (albumId != null) {
            val album = viewModel.queryAlbum(albumId)
            if (album != null && !TextUtils.isEmpty(album.picUrl)) {
                LoadImageUtil.loadFromNetwork(mActivity, album.picUrl, iv_cover)
                return
            }
        }

        iv_cover.setImageResource(R.mipmap.ic_empty_cover)
    }

    private val musicBroadcastListener = object : MusicServiceControl.MusicBroadcastListener {
        override fun onProgress(progress: Int) {
            if (progress < 0) {
                pb_progress.progress = 0
            } else {
                pb_progress.progress = progress
            }
        }

        override fun onCurrentMusicId(musicId: Long) {
            val music = viewModel.queryMusic(musicId) ?: return

            tv_music_name.text = music.name
            tv_artist.text = FormatUtil.formatArtistNames(music.artistNames)
            pb_progress.max = music.duration

            updateCover(music)
        }

        override fun onPlayState(flag: Boolean) {
            if (flag) {
                iv_play.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                iv_play.setImageResource(android.R.drawable.ic_media_play)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MusicControlFragment().apply {
            arguments = Bundle()
        }
    }
}
