package com.hardrubic.music.service

import android.content.*
import android.os.IBinder
import android.os.RemoteException
import com.hardrubic.music.Constant
import com.hardrubic.music.MusicManager
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.biz.adapter.MusicEntityAdapter
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.db.dataobject.Music
import com.hardrubic.music.util.LogUtil

class MusicServiceControl {
    private var musicBroadcastReceiver: MusicBroadcastReceiver? = null
    private var musicServiceConnection: ServiceConnection? = null
    private var musicManager: MusicManager? = null

    companion object {
        fun runInMusicService(context: Context, block: (MusicServiceControl) -> Unit) {
            val musicControl = MusicServiceControl()
            musicControl.register(context, connectCallback = {
                block(musicControl)
                musicControl.unregister(context)
            })
        }
    }

    fun register(context: Context, listener: MusicBroadcastListener? = null, connectCallback: (() -> Unit)? = null) {
        musicServiceConnection = MusicServiceConnection(connectCallback)

        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)

        if (listener != null) {
            musicBroadcastReceiver = MusicBroadcastReceiver(listener)
            val intentFilter = IntentFilter().apply {
                addAction(Constant.BroadcastAction.PROGRESS)
                addAction(Constant.BroadcastAction.CURRENT_MUSIC)
                addAction(Constant.BroadcastAction.PLAY_STATE)
            }

            val permission = "${context.packageName}.permission.${Constant.APP_NAME}"
            context.registerReceiver(musicBroadcastReceiver, intentFilter, permission, null)
        }
    }

    fun unregister(context: Context) {
        if (musicServiceConnection != null) {
            context.unbindService(musicServiceConnection)
            musicServiceConnection = null
        }

        if (musicBroadcastReceiver != null) {
            context.unregisterReceiver(musicBroadcastReceiver)
            musicBroadcastReceiver = null
        }
    }

    private inner class MusicServiceConnection(val connectCallback: (() -> Unit)?) : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.d("bind music service")
            musicManager = MusicManager.Stub.asInterface(service)
            connectCallback?.invoke()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicManager = null
            LogUtil.d("unbind music service")
        }
    }

    fun applySelectMusic(music: Music) {
        try {
            musicManager?.select(MusicEntityAdapter.toMusicAidl(music))
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyPlay() {
        try {
            musicManager?.play()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyPause() {
        try {
            musicManager?.pause()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applySeekTo(position: Int) {
        try {
            musicManager?.seekTo(position)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun isPlaying(): Boolean {
        try {
            return musicManager?.isPlaying ?: false
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return false
    }

    fun applyUpdateMusics(musics: List<Music>) {
        try {
            musicManager?.musics(musics.map { MusicEntityAdapter.toMusicAidl(it) })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyPrevious() {
        try {
            musicManager?.previous()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyNext() {
        try {
            musicManager?.next()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyStop() {
        try {
            musicManager?.stop()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyUpdatePlayModel(playModel: Int) {
        try {
            musicManager?.updatePlayModel(playModel)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyCurrentMusic() {
        try {
            musicManager?.applyCurrentMusic()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun applyPlayState() {
        try {
            musicManager?.applyPlayState()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private class MusicBroadcastReceiver(val listener: MusicBroadcastListener) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                Constant.BroadcastAction.PROGRESS -> {
                    val progress = intent.getIntExtra(Constant.Param.PROGRESS, -1)
                    //LogUtil.d("broadcast receive $action:$progress")
                    listener.onProgress(progress)
                }
                Constant.BroadcastAction.CURRENT_MUSIC -> {
                    val musicAidl = intent.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
                    CurrentPlayingHelper.setPlayingMusicId(musicAidl.musicId)
                    //LogUtil.d("broadcast receive $action:$musicAidl")
                    listener.onCurrentMusicId(musicAidl.musicId)
                }
                Constant.BroadcastAction.PLAY_STATE -> {
                    val flag = intent.getBooleanExtra(Constant.Param.FLAG, false)
                    //LogUtil.d("broadcast receive $action:$flag")
                    listener.onPlayState(flag)
                }
            }
        }
    }

    interface MusicBroadcastListener {
        fun onProgress(progress: Int)

        fun onCurrentMusicId(musicId: Long)

        fun onPlayState(flag: Boolean)
    }
}
