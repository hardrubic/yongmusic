package com.hardrubic.music.biz.command.list

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.service.MusicServiceControl
import java.util.*

class DeleteAllMusicsCommand(private val musicServiceControl: MusicServiceControl) : Command {

    override fun execute() {
        CurrentPlayingHelper.setPlayingMusicId(null)
        PlayListHelper.deleteAll()
        musicServiceControl.applyUpdateMusics(Collections.emptyList())
    }

}