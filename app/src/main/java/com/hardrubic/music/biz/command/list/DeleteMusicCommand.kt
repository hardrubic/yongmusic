package com.hardrubic.music.biz.command.list

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.service.MusicServiceControl

class DeleteMusicCommand(private val deleteMusicId: Long, private val musicRepository: MusicRepository, private val musicServiceControl: MusicServiceControl) : Command {

    override fun execute() {
        PlayListHelper.delete(deleteMusicId)
        val existIds = PlayListHelper.list()
        val musics = musicRepository.queryMusic(existIds)
        musicServiceControl.applyUpdateMusics(musics)

        val playingMusicId = CurrentPlayingHelper.getPlayingMusicId()
        if (playingMusicId == deleteMusicId) {
            CurrentPlayingHelper.setPlayingMusicId(playingMusicId)
            musicServiceControl.applyNext()
        }
    }

}