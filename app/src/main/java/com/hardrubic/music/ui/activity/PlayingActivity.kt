package com.hardrubic.music.ui.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.SeekBar
import android.widget.Toast
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.command.*
import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.biz.interf.MusicStateListener
import com.hardrubic.music.biz.vm.PlayingViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.ui.fragment.PlayListFragment
import com.hardrubic.music.ui.fragment.PlayingMusicMoreDialogFragment
import com.hardrubic.music.ui.widget.statusbar.StatusBarColor
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : BaseActivity(), MusicStateListener {

    private var movingProgress = false
    private var love: Boolean? = null

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

        iv_love.setOnClickListener {
            if (viewModel.playingMusic != null) {
                val playingMusicId = viewModel.playingMusic!!.musicId
                val newLove = !love!!
                updateLoveState(newLove)
                viewModel.changeMusicLove(playingMusicId, newLove)
            }
        }
        iv_download.setOnClickListener {

        }
        iv_more.setOnClickListener {
            if (viewModel.existPlayingMusic()) {
                val fragment = PlayingMusicMoreDialogFragment()
                fragment.show(supportFragmentManager, PlayingMusicMoreDialogFragment.TAG)
            } else {
                Toast.makeText(this, R.string.hint_no_playing_music, Toast.LENGTH_SHORT).show()
            }
        }

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
                    tv_position.text = FormatUtil.formatDuration(progress.toLong())
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
        tv_position.text = FormatUtil.formatDuration(progress.toLong())
    }

    override fun updateCurrentMusic(musicId: Long) {
        val music = viewModel.queryMusic(musicId) ?: return
        viewModel.playingMusic = music

        supportActionBar!!.title = music.name
        supportActionBar!!.subtitle = FormatUtil.formatArtistNames(music.artistNames)
        sb_progress.max = music.duration
        tv_duration.text = FormatUtil.formatDuration(music.duration.toLong())

        if (love == null) {
            love = viewModel.isMusicLove(music.musicId)
            updateLoveState(love!!)
        }

        updateCover(music)
    }

    private fun updateCover(music: Music) {
        val albumId = music.albumId ?: return

        val album = viewModel.queryAlbum(albumId) ?: return

        if (!TextUtils.isEmpty(album.picUrl)) {
            LoadImageUtil.loadFromNetwork(this, album.picUrl, iv_cover)
        }
    }

    override fun updatePlayingState(flag: Boolean) {
        if (flag) {
            iv_play.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            iv_play.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    private fun updateLoveState(love: Boolean) {
        if (love) {
            iv_love.setImageResource(R.mipmap.ic_love_red)
        } else {
            iv_love.setImageResource(R.mipmap.ic_love_white)
        }
    }

    private fun addMusic2Collection(collectionId: Long) {
        val musicId = viewModel.playingMusic?.musicId ?: return
        viewModel.addOrDelete2Collection(musicId, collectionId, true)

        val collection = viewModel.queryCollection(collectionId)
        collection?.let {
            val msg = getString(R.string.hint_add_music_to_collection_success, collection.name)
            Snackbar.make(sb_progress, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constant.RequestCode.SELECT_COLLECTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    val collectionId = data?.getLongExtra(Constant.Param.COLLECTION_ID, -1)
                            ?: return
                    addMusic2Collection(collectionId)
                }
            }
        }
    }
}
