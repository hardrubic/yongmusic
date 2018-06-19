package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class StopCommand : Command {
    override fun execute() {
        MusicControl.instance.applyStop()
    }

}
