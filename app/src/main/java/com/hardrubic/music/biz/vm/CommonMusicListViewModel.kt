package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.list.AddMusicsCommand
import com.hardrubic.music.biz.command.list.ReplaceMusicsCommand
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.entity.bo.MusicRelatedBO
import com.hardrubic.music.service.MusicServiceControl
import javax.inject.Inject

class CommonMusicListViewModel @Inject constructor(val application: Application,
                                                   val musicRepository: MusicRepository, val recentRepository: RecentRepository) : ViewModel() {
    fun queryMusics(musicIds: List<Long>): List<Music> {
        return musicRepository.queryMusic(musicIds)
    }

    fun saveMusicRelated(relatedBO: List<MusicRelatedBO>) {
        musicRepository.saveMusicRelated(relatedBO)
    }

    fun playMusic(musicId: Long) {
        val music = musicRepository.queryMusic(musicId)!!

        MusicServiceControl.runInMusicService(application) {
            RemoteControl.executeCommand(AddMusicsCommand(listOf(music), musicRepository, it))
            RemoteControl.executeCommand(SelectAndPlayCommand(music, recentRepository, it))
        }
    }

    fun playMusics(musics: List<Music>) {
        MusicServiceControl.runInMusicService(application) {
            RemoteControl.executeCommand(ReplaceMusicsCommand(musics, it))
            RemoteControl.executeCommand(SelectAndPlayCommand(musics.first(), recentRepository, it))
        }
    }
}