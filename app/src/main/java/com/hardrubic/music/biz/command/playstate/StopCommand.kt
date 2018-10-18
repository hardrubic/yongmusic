package com.hardrubic.music.biz.command.playstate

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.service.MusicServiceControl

class StopCommand(private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        musicServiceControl.applyStop()
    }

}
