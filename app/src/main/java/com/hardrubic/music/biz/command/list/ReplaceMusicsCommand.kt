package com.hardrubic.music.biz.command.list

import com.hardrubic.music.biz.command.Command
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl

/**
 * 更新PlayList并且更新remote
 */
class ReplaceMusicsCommand : Command {

    private var musics: List<Music>
    private var musicServiceControl: MusicServiceControl

    constructor(musics: List<Music>, musicServiceControl: MusicServiceControl) {
        this.musics = musics
        this.musicServiceControl = musicServiceControl
    }

    constructor(musicRepository: MusicRepository, musicServiceControl: MusicServiceControl) {
        this.musics = musicRepository.queryMusic(PlayListHelper.list())
        this.musicServiceControl = musicServiceControl
    }

    override fun execute() {
        PlayListHelper.replace(musics)
        musicServiceControl.applyUpdateMusics(musics)
    }

}