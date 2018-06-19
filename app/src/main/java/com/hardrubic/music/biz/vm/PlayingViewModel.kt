package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.command.NextCommand
import com.hardrubic.music.biz.command.PreviousCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.component.DaggerPlayingViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class PlayingViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    init {
        DaggerPlayingViewModelComponent.builder().build().inject(this)
    }

    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }

    fun previousMusic() {
        val preMusic = recentRepository.queryRecent() ?: return
        RemoteControl.executeCommand(PreviousCommand())
    }

    fun nextMusic() {
        RemoteControl.executeCommand(NextCommand())
    }
}