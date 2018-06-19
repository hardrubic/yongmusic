package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerRecentViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class RecentViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository

    val recentData = MutableLiveData<List<Music>>()

    init {
        DaggerRecentViewModelComponent.builder().build().inject(this)
    }

    fun recentMusicList() {
        recentData.value = recentRepository.queryRecentMusics()
    }

    fun selectRecentMusic(music: Music) {
        recentRepository.add(music.musicId)
        //添加到播放列表
        if (PlayListHelper.add(music)) {
            val musics = PlayListHelper.list().mapNotNull { musicRepository.queryMusic(it) }
            RemoteControl.executeCommand(UpdatePlayListCommand(musics))
        }
        //播放
        RemoteControl.executeCommand(SelectAndPlayCommand(music))
    }
}