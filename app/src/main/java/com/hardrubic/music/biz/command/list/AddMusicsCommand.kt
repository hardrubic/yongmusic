package com.hardrubic.music.biz.command.list

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl

class AddMusicsCommand(private val addMusics: List<Music>, private val musicRepository: MusicRepository,
                       private val musicServiceControl: MusicServiceControl) : Command {

    override fun execute() {
        if (PlayListHelper.add(addMusics)) {
            val musics = PlayListHelper.list().mapNotNull { musicRepository.queryMusic(it) }
            musicServiceControl.applyUpdateMusics(musics)
        }
    }

}