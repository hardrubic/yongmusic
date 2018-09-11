package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.entity.vo.MusicVO
import javax.inject.Inject

class RecentViewModel @Inject constructor(application: Application, val musicRepository: MusicRepository,
                      val recentRepository: RecentRepository) : AndroidViewModel(application) {

    val recentData = MutableLiveData<List<MusicVO>>()

    fun recentMusicList() {
        val recentMusic = recentRepository.queryRecentMusics()
        recentData.value = recentMusic.map { MusicEntityAdapter.toMusicVO(it) }
    }
}