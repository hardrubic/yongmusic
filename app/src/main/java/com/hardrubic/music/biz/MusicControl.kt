package com.hardrubic.music.biz

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hardrubic.music.aidl.MusicManager
import com.hardrubic.music.biz.adapter.MusicAidlAdapter
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.service.MusicService
import com.hardrubic.music.util.LogUtil

class MusicControl private constructor() {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MusicControl()
        }
    }

    private var musicManager: MusicManager? = null

    fun bindMusicService(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)

        //todo 刚启动service，传递播放列表、updatePplayModel、当前歌曲
    }

    fun unbindMusicService(context: Context) {
        context.unbindService(musicServiceConnection)
    }

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.d("bind music service")
            musicManager = MusicManager.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtil.d("unbind music service")
        }
    }

    fun applySelectMusic(music: Music) {
        musicManager?.select(MusicAidlAdapter.toMusicAidl(music))
    }

    fun applyPlayOrPause() {
        if (isPlaying()) {
            musicManager?.pause()
        } else {
            musicManager?.play()
        }
    }

    fun applySeekTo(position: Int) {
        musicManager?.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return musicManager?.isPlaying ?: false
    }

    fun applyUpdatePlayList(musics: List<Music>) {
        musicManager?.playList(musics.map { MusicAidlAdapter.toMusicAidl(it) })
    }

    fun applyPrevious() {
        musicManager?.previous()
    }

    fun applyNext() {
        musicManager?.next()
    }

    fun applyStop() {
        musicManager?.stop()
    }

    fun applyPlayModel(playModel: Int) {
        musicManager?.applyPlayModel(playModel)
    }

    fun applyCurrentMusic() {
        musicManager?.applyCurrentMusic()
    }

    fun applyPlayState() {
        musicManager?.applyPlayState()
    }
}
