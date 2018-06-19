package com.hardrubic.music.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hardrubic.music.Constant
import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.aidl.MusicManager
import com.hardrubic.music.biz.player.SeamlessMediaPlayer
import com.hardrubic.music.util.LogUtil
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * binder开发要点
 *
 * 1、main/aidl中创建MusicManager.aidl
 * 2、编译自动生成MusicManager.java，实现IInterface，里面包含唯一函数IBinder asBinder();
 * 3、MusicManager
 *   - DESCRIPTOR:Binder唯一标识
 *   - Stub和Proxy
 * 4、创建MusicManagerImpl，继承MusicManager.Stub，写具体处理逻辑
 */
class MusicService : Service() {

    private lateinit var mediaPlayer: SeamlessMediaPlayer
    private val musicManager = MusicManagerImpl()
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate() {
        super.onCreate()
        LogUtil.d("music service create")

        initMediaPlayer()
        sendProgressSchedule()
    }

    private fun initMediaPlayer() {
        mediaPlayer = SeamlessMediaPlayer()
        mediaPlayer.currentMusicLiveData.observeForever { music ->
            music?.let {
                sendCurrentMusic(it)
            }
        }
        mediaPlayer.playStateLiveData.observeForever { flag ->
            sendPlayState(flag ?: false)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("music service start command")

        //初始化默认参数
        intent?.let {
            val playList: List<MusicAidl>? = it.getParcelableArrayListExtra(Constant.Param.LIST)
            if (playList != null && playList.isNotEmpty()) {
                mediaPlayer.musicDispatch.updateMusics(playList)
            }

            val playingMusic = it.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
            if (playingMusic != null) {
                mediaPlayer.select(playingMusic)
                sendCurrentMusic(playingMusic)
                sendPlayState(false)
            }

            val playModel = it.getIntExtra(Constant.Param.PLAY_MODEL, Constant.PlayModel.LIST)
            mediaPlayer.updatePlayModel(playModel)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        LogUtil.d("music service bind")
        return musicManager
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtil.d("music service unbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d("music service destroy")

        //todo remove LiveData observer

        scheduledExecutorService.shutdownNow()
        mediaPlayer.destroy()
    }

    private fun sendProgressSchedule() {
        val delay = 0L
        val period = 1L
        scheduledExecutorService.scheduleAtFixedRate({
            if (mediaPlayer.isPlaying()) {
                val intent = Intent(Constant.BroadcastAction.PROGRESS)
                intent.putExtra(Constant.Param.PROGRESS, mediaPlayer.position())
                sendBroadcast(intent)
            }
        }, delay, period, TimeUnit.SECONDS)
    }

    private fun sendCurrentMusic(music: MusicAidl) {
        val intent = Intent(Constant.BroadcastAction.CURRENT_MUSIC)
        intent.putExtra(Constant.Param.CURRENT_MUSIC, music)

        sendBroadcast(intent)
    }

    private fun sendPlayState(flag: Boolean) {
        val intent = Intent(Constant.BroadcastAction.PLAY_STATE)
        intent.putExtra(Constant.Param.FLAG, flag)

        sendBroadcast(intent)
    }

    private inner class MusicManagerImpl : MusicManager.Stub() {
        override fun applyPlayModel(playModel: Int) {
            mediaPlayer.updatePlayModel(playModel)
        }

        override fun applyPlayState() {
            sendPlayState(mediaPlayer.playStateLiveData.value ?: false)
        }

        override fun applyCurrentMusic() {
            val currentMusic = mediaPlayer.currentMusicLiveData.value
            currentMusic?.let {
                sendCurrentMusic(currentMusic)
            }
        }

        override fun next() {
            mediaPlayer.next()
        }

        override fun previous() {
            mediaPlayer.previous()
        }

        override fun select(music: MusicAidl) {
            mediaPlayer.select(music)
        }

        override fun playList(musics: MutableList<MusicAidl>) {
            mediaPlayer.musicDispatch.updateMusics(musics)
        }

        override fun seekTo(position: Int) {
            mediaPlayer.seekTo(position)
        }

        override fun stop() {
            mediaPlayer.stop()

            //todo 反馈无音乐到界面
        }

        override fun isPlaying(): Boolean {
            return mediaPlayer.isPlaying()
        }

        override fun play() {
            mediaPlayer.play()
        }

        override fun pause() {
            mediaPlayer.pause()
        }
    }
}