package com.hardrubic.music.biz.command

import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.db.dataobject.Music

class ReplaceMusicsCommand(private val musics: List<Music>, private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        PlayListHelper.replace(musics)
        musicServiceControl.applyUpdateMusics(musics)
    }

}