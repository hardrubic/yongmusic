package com.hardrubic.music.ui.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.biz.command.*
import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.biz.vm.PlayingViewModel
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.ui.fragment.PlayListFragment
import com.hardrubic.music.ui.fragment.PlayingMusicMoreDialogFragment
import com.hardrubic.music.ui.widget.statusbar.StatusBarColor
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil
import dagger.android.AndroidInjector
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : BaseActivity() {

    private val musicServiceControl = MusicServiceControl()
    private var movingProgress = false

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    private val playingViewModel: PlayingViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PlayingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        initView()
    }

    override fun onStart() {
        super.onStart()
        musicServiceControl.register(this, musicBroadcastListener) {
            RemoteControl.executeCommand(ApplyCurrentMusicCommand(musicServiceControl))
            RemoteControl.executeCommand(ApplyPlayStateCommand(musicServiceControl))
        }
    }

    override fun onStop() {
        super.onStop()
        musicServiceControl.unregister(this)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        StatusBarColor.setStatusBarColor(this, ContextCompat.getColor(this, R.color.black))

        iv_love.setOnClickListener {
            val playingMusic = playingViewModel.playingMusic
            if (playingMusic == null) {
                Snackbar.make(sb_progress, R.string.hint_no_playing_music, Snackbar.LENGTH_SHORT).show()
            } else {
                val oldLove = playingViewModel.isMusicLove(playingMusic.musicId)
                val newLove = !oldLove
                updateLoveState(newLove)
                playingViewModel.changeMusicLove(playingMusic.musicId, newLove)
            }
        }
        iv_share.setOnClickListener {
            Snackbar.make(sb_progress, "TODO", Snackbar.LENGTH_SHORT).show()
        }
        iv_more.setOnClickListener {
            if (playingViewModel.existPlayingMusic()) {
                val fragment = PlayingMusicMoreDialogFragment()
                fragment.show(supportFragmentManager, PlayingMusicMoreDialogFragment.TAG)
            } else {
                Snackbar.make(sb_progress, R.string.hint_no_playing_music, Snackbar.LENGTH_SHORT).show()
            }
        }

        iv_previous.setOnClickListener {
            playingViewModel.previousMusic(musicServiceControl)
        }
        iv_next.setOnClickListener {
            playingViewModel.nextMusic(musicServiceControl)
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
                RemoteControl.executeCommand(SeekToCommand(seekBar!!.progress, musicServiceControl))
            }
        })

        val model = PlayModelHelper.loadPlayModel()
        iv_play_model.setImageDrawable(getDrawable(PlayModelHelper.getPlayModelIconId(model)))
    }

    private fun changePlayModel() {
        val currentPlayModel = PlayModelHelper.loadPlayModel()
        val nextPlayModel = PlayModelHelper.nextPlayModel(currentPlayModel)
        RemoteControl.executeCommand(LoopCommand(nextPlayModel, musicServiceControl))

        val iconId = PlayModelHelper.getPlayModelIconId(nextPlayModel)
        val stringId = PlayModelHelper.getPlayModelStringId(nextPlayModel)
        iv_play_model.setImageDrawable(getDrawable(iconId))
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show()
    }

    private fun updateCover(music: Music) {
        val circleRequestOption = RequestOptions.circleCropTransform()

        val albumId = music.albumId
        if (albumId != null) {
            val album = playingViewModel.queryAlbum(albumId)
            if (album != null && !TextUtils.isEmpty(album.picUrl)) {
                LoadImageUtil.loadFromNetwork(this, album.picUrl, iv_cover, circleRequestOption)
                return
            }
        }

        LoadImageUtil.loadFromResource(this, R.mipmap.ic_empty_cover, iv_cover, circleRequestOption)
    }

    private fun updateLoveState(love: Boolean) {
        if (love) {
            iv_love.setImageResource(R.mipmap.ic_love_red)
        } else {
            iv_love.setImageResource(R.mipmap.ic_love_white)
        }
    }

    private fun addMusic2Collection(collectionId: Long) {
        val musicId = playingViewModel.playingMusic?.musicId ?: return
        playingViewModel.addOrDelete2Collection(musicId, collectionId, true)

        val collection = playingViewModel.queryCollection(collectionId)
        collection?.let {
            val msg = getString(R.string.hint_add_music_to_collection_success, collection.name)
            Snackbar.make(sb_progress, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun refreshLoveState(musicId: Long) {
        val love = playingViewModel.isMusicLove(musicId)
        updateLoveState(love)
    }

    private val musicBroadcastListener = object : MusicServiceControl.MusicBroadcastListener {
        override fun onProgress(progress: Int) {
            if (movingProgress) {
                return
            }
            sb_progress.progress = progress
            tv_position.text = FormatUtil.formatDuration(progress.toLong())
        }

        override fun onCurrentMusicId(musicId: Long) {
            val music = playingViewModel.queryMusic(musicId) ?: return
            playingViewModel.playingMusic = music

            supportActionBar!!.title = music.name
            supportActionBar!!.subtitle = FormatUtil.formatArtistNames(music.artistNames)
            sb_progress.max = music.duration
            tv_duration.text = FormatUtil.formatDuration(music.duration.toLong())

            refreshLoveState(music.musicId)

            iv_cover.resetRotate()
            updateCover(music)
            iv_cover.startRotate()
        }

        override fun onPlayState(flag: Boolean) {
            if (flag) {
                iv_play.setImageResource(R.mipmap.ic_stop_white)
                iv_cover.startRotate()
            } else {
                iv_play.setImageResource(R.mipmap.ic_play_white)
                iv_cover.pauseRotate()
            }
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
                    refreshLoveState(playingViewModel.playingMusic!!.musicId)
                }
            }
        }
    }
}
