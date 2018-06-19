package com.hardrubic.music.biz.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.component.DaggerPlayListViewModelComponent
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.PreferencesUtil
import java.util.*
import javax.inject.Inject

class PlayListViewModel : ViewModel() {
    @Inject
    lateinit var musicRepository: MusicRepository
    val playListData = MutableLiveData<List<Music>>()

    init {
        DaggerPlayListViewModelComponent.builder().build().inject(this)
    }

    fun queryPlayList() {
        val ids = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)
        if (TextUtils.isEmpty(ids)) {
            playListData.value = Collections.emptyList()
        } else {
            val musics = musicRepository.queryMusic(ids!!.split(",").map { it.toLong() })
            playListData.value = musics
        }
    }

    fun selectMusic(music: Music) {
        RemoteControl.executeCommand(SelectAndPlayCommand(music))
    }

}