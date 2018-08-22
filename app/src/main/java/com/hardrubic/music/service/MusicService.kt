package com.hardrubic.music.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hardrubic.music.Constant
import com.hardrubic.music.MusicManager
import com.hardrubic.music.entity.aidl.MusicAidl
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
        LogUtil.d("MusicService:create")

        initMediaPlayer()
        initProgressSchedule()
    }

    private fun initMediaPlayer() {
        mediaPlayer = SeamlessMediaPlayer()
        mediaPlayer.currentMusicData.observeForever { music ->
            music?.let {
                sendCurrentMusic(it)
            }
        }
        mediaPlayer.playStateData.observeForever { flag ->
            sendPlayState(flag ?: false)
        }
    }

    private fun initProgressSchedule() {
        val delay = 0L
        val period = 1L
        scheduledExecutorService.scheduleAtFixedRate({
            sendProgress()
        }, delay, period, TimeUnit.SECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("MusicService:onStartCommand")

        //初始化默认参数
        intent?.let {
            val musics: List<MusicAidl>? = it.getParcelableArrayListExtra(Constant.Param.LIST)
            if (musics != null && musics.isNotEmpty()) {
                mediaPlayer.updateMusics(musics)
            }

            val playingMusic = it.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
            if (playingMusic != null) {
                mediaPlayer.select(playingMusic)
                //TODO
                //sendCurrentMusic(playingMusic)
                //sendPlayState(false)
            }

            val playModel = it.getIntExtra(Constant.Param.PLAY_MODEL, Constant.PlayModel.LIST)
            internalUpdatePlayModel(playModel)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        LogUtil.d("MusicService:onBind")
        return musicManager
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtil.d("MusicService:onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        LogUtil.d("MusicService:onDestroy")
        scheduledExecutorService.shutdownNow()
        mediaPlayer.destroy()
        super.onDestroy()
    }

    private fun sendProgress() {
        if (mediaPlayer.isPlaying()) {
            val intent = Intent(Constant.BroadcastAction.PROGRESS)
            intent.putExtra(Constant.Param.PROGRESS, mediaPlayer.position())
            sendBroadcast(intent)
        }
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
        override fun musics(musics: MutableList<MusicAidl>) {
            internalUpdateMusics(musics)
        }

        override fun updatePlayModel(playModel: Int) {
            internalUpdatePlayModel(playModel)
        }

        override fun applyPlayState() {
            sendPlayState(mediaPlayer.playStateData.value ?: false)
        }

        override fun applyCurrentMusic() {
            val currentMusic = mediaPlayer.currentMusicData.value
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

        override fun seekTo(position: Int) {
            mediaPlayer.seekTo(position)
        }

        override fun stop() {
            mediaPlayer.stop()
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

    private fun internalUpdatePlayModel(playModel: Int) {
        mediaPlayer.updatePlayModel(playModel)
    }

    private fun internalUpdateMusics(musics: List<MusicAidl>) {
        mediaPlayer.updateMusics(musics)
    }
}