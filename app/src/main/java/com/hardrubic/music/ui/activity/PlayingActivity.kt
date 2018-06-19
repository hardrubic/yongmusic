package com.hardrubic.music.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.SeekBar
import android.widget.Toast
import com.hardrubic.music.R
import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.biz.command.*
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.biz.listener.MusicStateListener
import com.hardrubic.music.biz.vm.PlayingViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.fragment.PlayListFragment
import com.hardrubic.music.ui.widget.statusbar.StatusBarColor
import com.hardrubic.music.util.FormatUtil
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : BaseActivity(), MusicStateListener {

    private var movingProgress = false

    private val viewModel: PlayingViewModel by lazy {
        ViewModelProviders.of(this).get(PlayingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        initData()
        initView()
    }

    private fun initData() {
        addMusicStateListener(this)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        StatusBarColor.setStatusBarColor(this, ContextCompat.getColor(this, R.color.black))

        iv_previous.setOnClickListener {
            viewModel.previousMusic()
        }
        iv_next.setOnClickListener {
            viewModel.nextMusic()
        }
        iv_play.setOnClickListener {
            RemoteControl.executeCommand(PlayOrPauseCommand())
        }
        iv_list.setOnClickListener {
            val fragment = PlayListFragment()
            fragment.show(supportFragmentManager, PlayListFragment.TAG)
        }
        iv_play_model.setOnClickListener {
            changePlayModel()
        }

        sb_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    tv_position.text = FormatUtil.formatDuration(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                movingProgress = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                movingProgress = false
                RemoteControl.executeCommand(SeekToCommand(seekBar!!.progress))
            }
        })

        val model = PlayModelHelper.loadPlayModel()
        iv_play_model.setImageDrawable(getDrawable(PlayModelHelper.getPlayModelIconId(model)))

        RemoteControl.executeCommand(ApplyCurrentMusicCommand())
        RemoteControl.executeCommand(ApplyPlayStateCommand())
    }

    private fun changePlayModel() {
        val currentPlayModel = PlayModelHelper.loadPlayModel()
        val nextPlayModel = PlayModelHelper.nextPlayModel(currentPlayModel)
        PlayModelHelper.savePlayModel(nextPlayModel)

        val iconId = PlayModelHelper.getPlayModelIconId(nextPlayModel)
        val stringId = PlayModelHelper.getPlayModelStringId(nextPlayModel)
        iv_play_model.setImageDrawable(getDrawable(iconId))
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show()
    }

    override fun updateProgress(progress: Int) {
        if (movingProgress) {
            return
        }
        sb_progress.progress = progress
        tv_position.text = FormatUtil.formatDuration(progress)
    }

    override fun updateCurrentMusic(music: Music) {
        supportActionBar!!.title = music.name
        supportActionBar!!.subtitle = music.artist
        sb_progress.max = music.duration
        tv_duration.text = FormatUtil.formatDuration(music.duration)
    }

    override fun updatePlayingState(flag: Boolean) {
        if (flag) {
            iv_play.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            iv_play.setImageResource(android.R.drawable.ic_media_play)
        }
    }

}
