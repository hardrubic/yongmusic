package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class LoopCommand(private val playModel:Int) : Command {
    override fun execute() {
        MusicControl.instance.applyPlayModel(playModel)
    }

}
