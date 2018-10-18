package com.hardrubic.music.biz.command.playstate

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.service.MusicServiceControl

class PlayOrPauseCommand(private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        if (musicServiceControl.isPlaying()) {
            RemoteControl.executeCommand(PauseCommand(musicServiceControl))
        } else {
            RemoteControl.executeCommand(PlayCommand(musicServiceControl))
        }
    }
}
