package com.hardrubic.music.biz.player

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.hardrubic.music.Constant
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.util.LogUtil

/**
 * 无缝播放
 */
class SeamlessMediaPlayer {
    private lateinit var currentMediaPlayer: MediaPlayer
    private var nextMediaPlayer: MediaPlayer? = null
    private val musicDispatch = MusicDispatch()

    val playStateData = MutableLiveData<Boolean>()
    val currentMusicData = MutableLiveData<MusicAidl>()

    private var currentMediaPlayerPrepare = false

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        currentMediaPlayer = MediaPlayer()
        currentMediaPlayer.setOnCompletionListener {
            LogUtil.d("MusicService:current music completed")
            this.currentMediaPlayer.release()

            consumeNextPlayMediaPlayer()
        }
    }

    private fun setCurrentMediaPlayer(music: MusicAidl) {
        this.currentMediaPlayer.reset()
        this.currentMediaPlayer.isLooping = musicDispatch.singlePlaying()
        this.currentMediaPlayer.setDataSource(music.path)
        this.currentMediaPlayer.prepare()
        this.currentMediaPlayerPrepare = true
        this.currentMediaPlayer.setOnErrorListener { mp, what, extra ->
            LogUtil.d("MusicService:setCurrentMediaPlayer error")
            true
        }
    }

    private fun setNextMediaPlayer() {
        val nextMusic = musicDispatch.next() ?: return
        this.nextMediaPlayer = MediaPlayer().apply {
            isLooping = musicDispatch.singlePlaying()
            setDataSource(nextMusic.path)
            prepare()
            setOnErrorListener { mp, what, extra ->
                LogUtil.d("MusicService:setNextMediaPlayer error")
                true
            }
        }
        this.currentMediaPlayer.setNextMediaPlayer(this.nextMediaPlayer)
    }

    private fun consumeNextPlayMediaPlayer() {
        if (nextMediaPlayer != null) {
            this.currentMediaPlayer = nextMediaPlayer!!
            this.currentMediaPlayerPrepare = true
            val nextMusic = this.musicDispatch.playNext()
            this.currentMusicData.postValue(nextMusic)
            play()
        }
        setNextMediaPlayer()
    }

    fun next() {
        if (musicDispatch.singlePlaying()) {
            seekTo(0)
        } else {
            stop()
            consumeNextPlayMediaPlayer()
        }
    }

    fun previous() {
        if (musicDispatch.singlePlaying()) {
            seekTo(0)
        } else {
            stop()
            val music = musicDispatch.previous() ?: return
            select(music)
        }
    }

    fun select(music: MusicAidl) {
        this.currentMusicData.postValue(music)
        this.musicDispatch.playNext(music)

        setCurrentMediaPlayer(music)
        setNextMediaPlayer()
        play()
    }

    fun play() {
        if (currentMediaPlayerPrepare) {
            this.currentMediaPlayer.start()
            this.playStateData.postValue(true)
        }
    }

    fun pause() {
        if (isPlaying()) {
            currentMediaPlayer.pause()
            this.playStateData.postValue(false)
        }
    }

    fun stop() {
        this.currentMediaPlayer.stop()
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
        this.currentMediaPlayer.release()
        this.nextMediaPlayer?.release()
    }

    fun updatePlayModel(playModel: Int) {
        LogUtil.d("MusicService:playModel[$playModel]")

        val isSingle = playModel == Constant.PlayModel.SINGLE
        this.currentMediaPlayer.isLooping = isSingle
        this.musicDispatch.playModel = playModel

        //change next music
        if (currentMediaPlayerPrepare) {
            if (musicDispatch.singlePlaying()) {
                this.currentMediaPlayer.setNextMediaPlayer(null)
                this.nextMediaPlayer?.reset()
            } else {
                setNextMediaPlayer()
            }
        }
    }

    fun updateMusics(musics: List<MusicAidl>) {
        LogUtil.d("MusicService:updateMusics[${musics.size}]")
        this.musicDispatch.musics = musics
    }
}