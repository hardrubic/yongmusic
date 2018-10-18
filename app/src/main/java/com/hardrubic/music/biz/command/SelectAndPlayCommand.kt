package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.command.playstate.PlayCommand
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl

class SelectAndPlayCommand(private val music: Music, private val recentRepository: RecentRepository,
                           private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        recentRepository.add(music.musicId)
        RemoteControl.executeCommand(SelectCommand(music, musicServiceControl))
        RemoteControl.executeCommand(PlayCommand(musicServiceControl))
    }
}
