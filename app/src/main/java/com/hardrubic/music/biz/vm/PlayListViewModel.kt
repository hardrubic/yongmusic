package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import com.hardrubic.music.Constant
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.list.DeleteAllMusicsCommand
import com.hardrubic.music.biz.command.list.DeleteMusicCommand
import com.hardrubic.music.biz.command.playstate.StopCommand
import com.hardrubic.music.biz.helper.MusicHelper
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicServiceControl
import com.hardrubic.music.util.PreferencesUtil
import java.util.*
import javax.inject.Inject

class PlayListViewModel @Inject constructor(val application: Application, val musicRepository: MusicRepository,
                                            val recentRepository: RecentRepository) : ViewModel() {
    val playListData = MutableLiveData<List<Music>>()

    fun queryPlayList() {
        val ids = PreferencesUtil.instance.getString(Constant.SpKey.PLAY_LIST)
        if (TextUtils.isEmpty(ids)) {
            playListData.value = Collections.emptyList()
        } else {
            val initialMusicIds = ids.split(",").map { it.toLong() }
            val musics = musicRepository.queryMusic(initialMusicIds)
            playListData.value = MusicHelper.sortMusicByTargetId(musics, initialMusicIds)
        }
    }

    fun selectMusic(music: Music) {
        MusicServiceControl.runInMusicService(application) {
            RemoteControl.executeCommand(SelectAndPlayCommand(music, recentRepository, it))
        }
    }

    fun deleteMusic(deleteMusicId: Long) {
        MusicServiceControl.runInMusicService(application) {
            RemoteControl.executeCommand(DeleteMusicCommand(deleteMusicId, musicRepository, it))
        }
    }

    fun deleteAllMusic() {
        MusicServiceControl.runInMusicService(application) {
            RemoteControl.executeCommand(DeleteAllMusicsCommand(it))
            RemoteControl.executeCommand(StopCommand(it))
        }
    }

}