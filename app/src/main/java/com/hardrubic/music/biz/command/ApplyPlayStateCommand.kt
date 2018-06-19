package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.db.dataobject.Music

class ApplyPlayStateCommand : Command {
    override fun execute() {
        MusicControl.instance.applyPlayState()
    }

}