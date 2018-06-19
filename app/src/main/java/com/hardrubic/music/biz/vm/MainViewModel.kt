package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.hardrubic.music.biz.component.DaggerMainViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Music
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var musicRepository: MusicRepository


    init {
        DaggerMainViewModelComponent.builder().build().inject(this)
    }

    fun queryMusic(musicId: Long): Music? {
        return musicRepository.queryMusic(musicId)
    }
}