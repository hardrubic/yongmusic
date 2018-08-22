package com.hardrubic.music.biz.command

import com.hardrubic.music.service.MusicServiceControl

class NextCommand(private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        musicServiceControl.applyNext()
    }
}
