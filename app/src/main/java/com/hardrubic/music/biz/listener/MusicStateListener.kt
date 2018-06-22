package com.hardrubic.music.biz.listener

interface MusicStateListener {
    fun updateProgress(progress: Int)

    fun updateCurrentMusic(musicId: Long)

    fun updatePlayingState(flag: Boolean)
}