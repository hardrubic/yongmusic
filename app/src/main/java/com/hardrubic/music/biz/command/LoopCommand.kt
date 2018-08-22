package com.hardrubic.music.biz.command

import com.hardrubic.music.biz.helper.PlayModelHelper
import com.hardrubic.music.service.MusicServiceControl

class LoopCommand(private val playModel: Int, private val musicServiceControl: MusicServiceControl) : Command {
    override fun execute() {
        PlayModelHelper.updatePlayModel(playModel)
        musicServiceControl.applyUpdatePlayModel(playModel)
    }

}
