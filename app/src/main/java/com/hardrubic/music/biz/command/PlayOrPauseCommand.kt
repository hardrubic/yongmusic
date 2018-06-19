package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class PlayOrPauseCommand : Command {
    override fun execute() {
        MusicControl.instance.applyPlayOrPause()
    }

}
