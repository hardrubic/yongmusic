package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl

class SelectCommand(private val music: Music, private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        CurrentPlayingHelper.setPlayingMusicId(music.musicId)
        musicServiceControl.applySelectMusic(music)
    }
}