package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.MusicControl

class SeekToCommand(private val position: Int) : Command {
    override fun execute() {
        MusicControl.instance.applySeekTo(position)
    }

}
