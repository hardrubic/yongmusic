package com.hardrubic.music.biz.command

import com.hardrubic.music.service.MusicServiceControl

class ApplyPlayStateCommand(private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        musicServiceControl.applyPlayState()
    }

}