package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.db.dataobject.Music

class UpdatePlayListCommand(private val musics: List<Music>) : Command {
    override fun execute() {
        MusicControl.instance.applyUpdatePlayList(musics)
    }

}