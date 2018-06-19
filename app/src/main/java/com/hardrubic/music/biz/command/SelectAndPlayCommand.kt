package com.hardrubic.music.biz.command

import com.hardrubic.music.db.dataobject.Music

class SelectAndPlayCommand(private val music: Music) : Command {
    override fun execute() {
        RemoteControl.executeCommand(SelectCommand(music))
        RemoteControl.executeCommand(PlayOrPauseCommand())
    }
}
