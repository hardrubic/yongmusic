package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.db.dataobject.Music

class SelectCommand(private val music: Music) : Command {
    override fun execute() {
        CurrentPlayingHelper.setPlayingMusicId(music.musicId)
        MusicControl.instance.applySelectMusic(music)
    }

}