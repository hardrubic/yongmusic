package com.hardrubic.music.biz.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.hardrubic.music.biz.command.RemoteControl
import com.hardrubic.music.biz.command.SelectAndPlayCommand
import com.hardrubic.music.biz.command.UpdatePlayListCommand
import com.hardrubic.music.biz.component.DaggerCommonMusicListViewModelComponent
import com.hardrubic.music.biz.component.DaggerSearchViewModelComponent
import com.hardrubic.music.biz.helper.PlayListHelper
import com.hardrubic.music.biz.repository.AlbumRepository
import com.hardrubic.music.biz.repository.ArtistRepository
import com.hardrubic.music.biz.repository.MusicRepository
import com.hardrubic.music.biz.repository.RecentRepository
import com.hardrubic.music.db.dataobject.Album
import com.hardrubic.music.db.dataobject.Artist
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.util.FileUtil
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import java.util.*
import javax.inject.Inject

class CommonMusicListViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var musicRepository: MusicRepository
    @Inject
    lateinit var recentRepository: RecentRepository
    @Inject
    lateinit var artistRepository: ArtistRepository
    @Inject
    lateinit var albumRepository: AlbumRepository

    init {
        DaggerCommonMusicListViewModelComponent.builder().build().inject(this)
    }

    fun applyCacheAndPlayMusic(musicId: Long) {
        HttpService.instance.applyMusicResource(musicId)
                .subscribe(object : SingleObserver<Pair<String, String>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(pair: Pair<String, String>) {
                        //cacheMusicResource(music, pair.first, pair.second)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })

    }

    private fun cacheMusicResource(music: Music, url: String, md5: String) {
        val savePath = FileUtil.getMusicCacheDir(getApplication())
        val saveName = md5

        val mission = Mission(url, saveName, savePath)
        RxDownload.create(mission, true).subscribe(Consumer<Status> { status ->
            music.path = savePath + saveName

            when (status) {
                is Succeed -> finishCacheMusic(music)
            }
        }, Consumer<Throwable> { throwable -> throwable.printStackTrace() })
    }

    private fun finishCacheMusic(music: Music) {
        recentRepository.add(music.musicId)
        musicRepository.add(music)
        artistRepository.add(music.artists)
        albumRepository.add(music.album)
        //添加到播放列表
        if (PlayListHelper.add(music)) {
            val musics = PlayListHelper.list().mapNotNull { musicRepository.queryMusic(it) }
            RemoteControl.executeCommand(UpdatePlayListCommand(musics))
        }
        //播放
        RemoteControl.executeCommand(SelectAndPlayCommand(music))
    }
}