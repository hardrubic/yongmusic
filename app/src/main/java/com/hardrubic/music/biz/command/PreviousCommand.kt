package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class PreviousCommand() : Command {
    override fun execute() {
        MusicControl.instance.applyPrevious()
    }

}
