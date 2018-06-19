package com.hardrubic.music.biz.listener

import com.hardrubic.music.db.dataobject.Music

interface MusicStateListener {
    fun updateProgress(progress: Int)

    fun updateCurrentMusic(music: Music)

    fun updatePlayingState(flag: Boolean)
}