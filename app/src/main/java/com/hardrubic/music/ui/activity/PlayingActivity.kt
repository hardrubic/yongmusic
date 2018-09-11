package com.hardrubic.music.ui.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.SeekBar
import android.widget.Toast
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
import com.hardrubic.music.util.DrawableUtil
import com.hardrubic.music.util.FormatUtil
import com.hardrubic.music.util.LoadImageUtil
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_playing.*
import javax.inject.Inject

class PlayingActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private val musicServiceControl = MusicServiceControl()
    private var movingProgress = false
    private var love: Boolean? = null

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PlayingViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PlayingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        AndroidInjection.inject(this)
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
            viewModel.previousMusic(musicServiceControl)
        }
        iv_next.setOnClickListener {
            viewModel.nextMusic(musicServiceControl)
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
        DrawableUtil.setImageViewColor(iv_list, R.color.white)
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
        val albumId = music.albumId ?: return

        val album = viewModel.queryAlbum(albumId) ?: return

        if (!TextUtils.isEmpty(album.picUrl)) {
            LoadImageUtil.loadFromNetwork(this, album.picUrl, iv_cover)
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

    private val musicBroadcastListener = object : MusicServiceControl.MusicBroadcastListener {
        override fun onProgress(progress: Int) {
            if (movingProgress) {
                return
            }
            sb_progress.progress = progress
            tv_position.text = FormatUtil.formatDuration(progress.toLong())
        }

        override fun onCurrentMusicId(musicId: Long) {
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

            iv_cover.resetRotate()
            updateCover(music)
        }

        override fun onPlayState(flag: Boolean) {
            if (flag) {
                iv_play.setImageResource(android.R.drawable.ic_media_pause)
                iv_cover.startRotate()
            } else {
                iv_play.setImageResource(android.R.drawable.ic_media_play)
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
                }
            }
        }
    }
}
