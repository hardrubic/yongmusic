package com.hardrubic.music.biz.player

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.hardrubic.music.Constant
import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.util.LogUtil

/**
 * 无缝播放
 */
class SeamlessMediaPlayer : MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private lateinit var currentMediaPlayer: MediaPlayer
    private var nextMediaPlayer: MediaPlayer? = null
    val musicDispatch = MusicDispatch()

    val playStateLiveData = MutableLiveData<Boolean>()
    val currentMusicLiveData = MutableLiveData<MusicAidl>()
    private var nextMusic: MusicAidl? = null

    private var currentMediaPlayerPrepare = false

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        currentMediaPlayer = MediaPlayer()
        currentMediaPlayer.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        LogUtil.d("current completed")
        this.currentMediaPlayer.release()

        //change to next music
        if (nextMediaPlayer != null && nextMusic != null) {
            this.currentMediaPlayer = nextMediaPlayer!!
            this.musicDispatch.playing(nextMusic!!)
            this.currentMusicLiveData.postValue(nextMusic)
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        //todo
        return true
    }

    fun next() {
        stop()
        val music = musicDispatch.next() ?: return
        select(music)
        play()
    }

    fun previous() {
        stop()
        val music = musicDispatch.previous() ?: return
        select(music)
        play()
    }

    fun select(music: MusicAidl) {
        this.currentMusicLiveData.postValue(music)
        this.musicDispatch.playing(music)

        setCurrentMediaPlayer(music)

        val nextMusic = musicDispatch.next()
        nextMusic?.let {
            setNextMediaPlayer(it)
        }
    }

    private fun setCurrentMediaPlayer(music: MusicAidl) {
        //todo 异常处理

        this.currentMediaPlayer.reset()
        this.currentMediaPlayer.isLooping = musicDispatch.singlePlaying()
        this.currentMediaPlayer.setDataSource(music.path)
        this.currentMediaPlayer.prepare()
        this.currentMediaPlayerPrepare = true
    }

    private fun setNextMediaPlayer(music: MusicAidl) {
        if (musicDispatch.singlePlaying()) {
            return
        }

        this.nextMusic = music

        if (nextMediaPlayer != null) {
            nextMediaPlayer?.release()
        }

        this.nextMediaPlayer = MediaPlayer().apply {
            isLooping = musicDispatch.singlePlaying()
            setDataSource(music.path)
            prepare()

            setOnErrorListener { mp, what, extra ->
                //todo
                true
            }
        }
        this.currentMediaPlayer.setNextMediaPlayer(this.nextMediaPlayer)
    }

    fun play() {
        if (currentMediaPlayerPrepare) {
            this.currentMediaPlayer.start()
            this.playStateLiveData.postValue(true)
        }
    }

    fun pause() {
        if (isPlaying()) {
            currentMediaPlayer.pause()
            this.playStateLiveData.postValue(false)
        }
    }

    fun stop() {
        this.currentMediaPlayer.stop()
    }

    fun updatePlayModel(playModel: Int) {
        LogUtil.d("single play:$playModel")

        val isSingle = playModel == Constant.PlayModel.SINGLE
        this.currentMediaPlayer.isLooping = isSingle
        this.nextMediaPlayer?.isLooping = isSingle
        this.musicDispatch.updatePlayModel(playModel)

        val nextMusic = musicDispatch.next()
        nextMusic?.let {
            setNextMediaPlayer(it)
        }
    }

    fun isPlaying(): Boolean {
        return this.currentMediaPlayer.isPlaying
    }

    fun seekTo(position: Int) {
        if (isPlaying()) {
            this.currentMediaPlayer.seekTo(position)
        }
    }

    fun position(): Int {
        return this.currentMediaPlayer.currentPosition
    }

    fun duration(): Int {
        return this.currentMediaPlayer.duration
    }

    fun destroy() {
        //todo
    }
}