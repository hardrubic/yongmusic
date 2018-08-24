package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.component.DaggerPlayListViewModelComponent
import com.hardrubic.music.biz.helper.MusicHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.util.PreferencesUtil
import java.util.*
import javax.inject.Inject

class PlayListViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository
    val playListData = MutableLiveData<List<Music>>()

    init {
        DaggerPlayListViewModelComponent.builder().build().inject(this)
    }

    fun queryPlayList() {
        val ids = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)
        if (TextUtils.isEmpty(ids)) {
            playListData.value = Collections.emptyList()
        } else {
            val initialMusicIds = ids!!.split(",").map { it.toLong() }
            val musics = musicRepository.queryMusic(initialMusicIds)
            playListData.value = MusicHelper.sortMusicByInitialId(musics, initialMusicIds)
        }
    }

    fun selectMusic(music: Music) {
        MusicServiceControl.runInMusicService(getApplication()) {
            RemoteControl.executeCommand(SelectAndPlayCommand(music, recentRepository, it))
        }
    }

}