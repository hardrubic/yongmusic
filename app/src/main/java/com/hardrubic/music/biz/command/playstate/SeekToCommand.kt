package com.hardrubic.music.biz.command.playstate

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.service.MusicServiceControl

class SeekToCommand(private val position: Int, private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        musicServiceControl.applySeekTo(position)
    }

}
