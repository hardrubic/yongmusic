package com.hardrubic.music.biz.helper

import com.hardrubic.music.Constant
import com.hardrubic.music.util.PreferencesUtil

object CurrentPlayingHelper {
    fun getPlayingMusicId(): Long? {
        val id = PreferencesUtil.instance.getLong(Constant.SpKey.PLAYING)
        if (id == -1L) {
            return null
        }
        return id
    }


    fun setPlayingMusicId(musicId: Long?) {
        if (musicId == null) {
            PreferencesUtil.instance.deleteKey(Constant.SpKey.PLAYING)
        } else {
            PreferencesUtil.instance.putLong(Constant.SpKey.PLAYING, musicId)
        }
    }
}