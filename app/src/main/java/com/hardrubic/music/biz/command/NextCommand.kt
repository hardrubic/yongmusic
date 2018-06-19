package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class NextCommand() : Command {
    override fun execute() {
        MusicControl.instance.applyNext()
    }

}
