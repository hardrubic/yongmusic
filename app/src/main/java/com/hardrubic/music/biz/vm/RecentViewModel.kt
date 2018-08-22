package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.command.AddMusicsCommand
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.component.DaggerRecentViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.entity.vo.MusicVO
import com.hardrubic.music.service.MusicServiceControl
import javax.inject.Inject

class RecentViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    val recentData = MutableLiveData<List<MusicVO>>()

    init {
        DaggerRecentViewModelComponent.builder().build().inject(this)
    }

    fun recentMusicList() {
        val recentMusic = recentRepository.queryRecentMusics()
        recentData.value = recentMusic.map { MusicEntityAdapter.toMusicVO(it) }
    }

    fun selectMusic(musicIds: List<Long>, playMusicId: Long = musicIds.first()) {
        val musics = musicRepository.queryMusic(musicIds)
        val playMusic = musicRepository.queryMusic(playMusicId)

        MusicServiceControl.runInMusicService(getApplication()) {
            RemoteControl.executeCommand(AddMusicsCommand(musics, musicRepository, it))
            if (playMusic != null) {
                RemoteControl.executeCommand(SelectAndPlayCommand(playMusic, recentRepository, it))
            }
        }
    }
}