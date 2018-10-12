package com.hardrubic.music.biz.player

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.service.MusicService
import java.io.File

/**
 * 无缝播放
 */
class SeamlessMediaPlayer {
    private lateinit var currentMediaPlayer: MediaPlayer
    private lateinit var nextMediaPlayer: MediaPlayer
    private var nextMediaPlayerPrePrepared = false
    private val musicDispatch = MusicDispatch()

    val currentMusicData = MutableLiveData<MusicAidl>()

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        currentMediaPlayer = MediaPlayer()
        currentMediaPlayer.setOnCompletionListener {
            completeMusic()
        }
        currentMediaPlayer.setOnErrorListener { mp, what, extra ->
            MusicService.debugInner("currentMediaPlayer error")
            true
        }

        nextMediaPlayer = MediaPlayer()
        nextMediaPlayer.setOnCompletionListener {
            completeMusic()
        }
        nextMediaPlayer.setOnErrorListener { mp, what, extra ->
            MusicService.debugInner("setNextMediaPlayer error")
            true
        }
    }

    private fun setCurrentMediaPlayer(music: MusicAidl) {
        currentMediaPlayer.reset()
        currentMediaPlayer.isLooping = musicDispatch.singlePlaying()
        currentMediaPlayer.setDataSource(music.path)
        currentMediaPlayer.prepare()
        MusicService.debugMusic("触发播放歌曲[${music.name}]")
    }

    private fun setNextMediaPlayer() {
        val nextMusic = musicDispatch.next()
        if (nextMusic == null) {
            nextMediaPlayerPrePrepared = false
            return
        }

        if (!varifyMusic(nextMusic)) {
            nextMediaPlayerPrePrepared = false
            return
        }
        nextMediaPlayerPrePrepared = true

        nextMediaPlayer.reset()
        nextMediaPlayer.isLooping = musicDispatch.singlePlaying()
        nextMediaPlayer.setDataSource(nextMusic.path)
        nextMediaPlayer.prepare()

        currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer)
        MusicService.debugMusic("设置下一首歌曲[${nextMusic.name}]")
    }

    private fun varifyMusic(music: MusicAidl): Boolean {
        val file = File(music.path)
        if (!file.exists() || !file.isFile) {
            MusicService.debugMusic("[${music.name}-${music.path}]校验不通过")
            return false
        }
        return true
    }

    private fun completeMusic() {
        MusicService.debugMusic("[${musicDispatch.playingMusic!!.name}]播放完毕")
        consumeNextMusic()
    }

    private fun consumeNextMusic() {
        if (!nextMediaPlayerPrePrepared) {
            MusicService.debugMusic("没有下一首歌")
            return
        }

        //swap
        val tmp = nextMediaPlayer
        nextMediaPlayer = currentMediaPlayer
        currentMediaPlayer = tmp

        val music = this.musicDispatch.playNext()!!
        this.currentMusicData.postValue(music)
        play()
        MusicService.debugMusic("播放下一首歌曲[${music.name}]")

        setNextMediaPlayer()
    }

    fun next() {
        if (musicDispatch.singlePlaying()) {
            seekTo(0)
        } else {
            stop()
            consumeNextMusic()
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

    /**
     * @return whether to change the state
     */
    fun play(): Boolean {
        if (!isPlaying()) {
            currentMediaPlayer.start()
            return true
        }
        return false
    }

    /**
     * @return whether to change the state
     */
    fun pause(): Boolean {
        if (isPlaying()) {
            currentMediaPlayer.pause()
            return true
        }
        return false
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
        currentMediaPlayer.release()
        nextMediaPlayer.release()
    }

    fun updatePlayModel(playModel: Int) {
        MusicService.debugMusic("更新循环模式[${PlayModelHelper.debugPlayModelName(playModel)}]")

        val isSingle = playModel == Constant.PlayModel.SINGLE
        currentMediaPlayer.isLooping = isSingle
        nextMediaPlayer.isLooping = isSingle
        musicDispatch.playModel = playModel

        //change next music
        if (isSingle) {
            currentMediaPlayer.setNextMediaPlayer(null)
            nextMediaPlayer.reset()
        } else {
            setNextMediaPlayer()
        }
    }

    fun updateMusics(musics: List<MusicAidl>) {
        if (musics.isEmpty()) {
            MusicService.debugMusic("清空播放列表")
        } else {
            MusicService.debugMusic("更新播放列表[${musics.joinToString(separator = ",") { it.name }}]")
        }
        musicDispatch.musics = musics
    }
}