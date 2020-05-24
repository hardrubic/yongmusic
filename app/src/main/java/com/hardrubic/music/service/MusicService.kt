package com.hardrubic.music.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.widget.RemoteViews
import com.hardrubic.music.Constant
import com.hardrubic.music.MusicManager
import com.hardrubic.music.R
import com.hardrubic.music.biz.player.SeamlessMediaPlayer
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.ui.activity.PlayingActivity
import com.hardrubic.music.util.LoadImageUtil
import com.hardrubic.music.util.LogUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
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

    companion object {
        val TAG_INNER_STATE = "debug_inner"
        val TAG_DEBUG_MUSIC = "debug_music"

        fun debugInner(text: String) {
            LogUtil.d("$TAG_INNER_STATE-$text")
        }

        fun debugMusic(text: String) {
            LogUtil.d("$TAG_DEBUG_MUSIC-$text")
        }
    }

    private lateinit var mediaPlayer: SeamlessMediaPlayer
    private val musicManager = MusicManagerImpl()
    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var notifBroadcaseReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        debugInner("service onCreate")

        initMediaPlayer()
        initProgressSchedule()
        registerNotifBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        debugInner("service onStartCommand")

        //初始化默认参数
        val playModel = intent?.getIntExtra(Constant.Param.PLAY_MODEL, Constant.PlayModel.LIST)!!
        internalUpdatePlayModel(playModel)

        val musics: List<MusicAidl>? = intent?.getParcelableArrayListExtra(Constant.Param.LIST)
        if (musics != null && musics.isNotEmpty()) {
            mediaPlayer.updateMusics(musics)
        }

        val playingMusic = intent?.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
        if (playingMusic != null) {
            mediaPlayer.select(playingMusic)
            //TODO
            //sendCurrentMusic(playingMusic)
            //sendPlayState(false)
        }

        //通知
        createMusicNotificationChannel()
        changeMusicNotification()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        debugInner("service onBind")
        return musicManager
    }

    override fun onUnbind(intent: Intent?): Boolean {
        debugInner("service onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        debugInner("service onDestroy")
        unregisterNotifBroadcastReceiver()
        scheduledExecutorService.shutdownNow()
        mediaPlayer.destroy()
        super.onDestroy()
    }

    private fun registerNotifBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Constant.BroadcastAction.PLAY_NEXT)
            addAction(Constant.BroadcastAction.PLAY_PRE)
            addAction(Constant.BroadcastAction.PLAY_OR_PAUSE)
        }

        notifBroadcaseReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    Constant.BroadcastAction.PLAY_NEXT -> {
                        mediaPlayer.next()
                    }
                    Constant.BroadcastAction.PLAY_PRE -> {
                        mediaPlayer.previous()
                    }
                    Constant.BroadcastAction.PLAY_OR_PAUSE -> {
                        mediaPlayer.playOrPause()
                        changeMusicNotification()
                    }
                }
            }

        }
        registerReceiver(notifBroadcaseReceiver, intentFilter)
    }

    private fun unregisterNotifBroadcastReceiver() {
        if (notifBroadcaseReceiver != null) {
            unregisterReceiver(notifBroadcaseReceiver)
            notifBroadcaseReceiver = null
        }
    }

    private fun createMusicNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = Constant.NotificationChannel.MUSIC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8 need channel object
            val channelName = "music channel"
            val channelDesc = "music channel desc"
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, channelImportance)
            channel.description = channelDesc
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun creteMusicNotification(music: MusicAidl?, playing: Boolean): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.layout_music_notification)
        if (music != null && !TextUtils.isEmpty(music.coverUrl)) {
            val bitmap = LoadImageUtil.loadFromNetworkAsBitmap(this, music.coverUrl)
            remoteViews.setImageViewBitmap(R.id.iv_photo, bitmap)
            remoteViews.setTextViewText(R.id.tv_name, music.name)
            remoteViews.setTextViewText(R.id.tv_artist, "${music.artistNames}-${music.albumName}")
            remoteViews.setImageViewResource(R.id.iv_play, if (playing) R.mipmap.ic_stop_black else R.mipmap.ic_play_black)

            //PendingIntent是指把Intent包装了一层, 放入一个新的进程. 通过触发事件去触发
            remoteViews.setOnClickPendingIntent(R.id.iv_play, buildNotiBroadcastPendingIntent(Constant.PendingRequestCode.PLAY_OR_PAUSE, Constant.BroadcastAction.PLAY_OR_PAUSE))
            remoteViews.setOnClickPendingIntent(R.id.iv_previous, buildNotiBroadcastPendingIntent(Constant.PendingRequestCode.PLAY_PRE, Constant.BroadcastAction.PLAY_PRE))
            remoteViews.setOnClickPendingIntent(R.id.iv_next, buildNotiBroadcastPendingIntent(Constant.PendingRequestCode.PLAY_NEXT, Constant.BroadcastAction.PLAY_NEXT))
        } else {
            //TODO 优化没有音乐时的展示
            remoteViews.setImageViewBitmap(R.id.iv_photo, BitmapFactory.decodeResource(resources, R.mipmap.ic_empty_cover))
            remoteViews.setTextViewText(R.id.tv_name, getString(R.string.no_music))
        }
        return remoteViews
    }

    private fun changeMusicNotification() {
        val channelId = Constant.NotificationChannel.MUSIC
        val playing = mediaPlayer.isPlaying()
        val music = mediaPlayer.currentMusicData.value

        val intent = Intent(this, PlayingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val playingActivityPendingIntent = PendingIntent.getActivity(this,
                Constant.PendingRequestCode.OPEN_PLAYING, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        Single.create<RemoteViews> {
            it.onSuccess(creteMusicNotification(music, playing))
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setCustomBigContentView(it)
                            .setContentIntent(playingActivityPendingIntent)
                            .setWhen(System.currentTimeMillis())
                            .build()
                }
                .subscribe(Consumer {
                    startForeground(1, it)
                })
    }

    private fun initMediaPlayer() {
        mediaPlayer = SeamlessMediaPlayer()
        mediaPlayer.currentMusicData.observeForever { music ->
            music?.let {
                doMusicChange(it)
            }
        }
    }

    private fun initProgressSchedule() {
        val delay = 0L
        val period = 1L
        scheduledExecutorService.scheduleAtFixedRate({
            sendProgress()
        }, delay, period, TimeUnit.SECONDS)
    }

    private fun doMusicChange(playingMusic: MusicAidl) {
        changeMusicNotification()
        sendCurrentMusic(playingMusic)
        OverlayServiceControl.changeMusicName(this, playingMusic.name)
    }

    private fun sendProgress() {
        if (mediaPlayer.isPlaying()) {
            val intent = Intent(Constant.BroadcastAction.PROGRESS)
            intent.putExtra(Constant.Param.PROGRESS, mediaPlayer.position())
            sendBroadcastWithPermission(intent)
        }
    }

    private fun sendCurrentMusic(music: MusicAidl) {
        val intent = Intent(Constant.BroadcastAction.CURRENT_MUSIC)
        intent.putExtra(Constant.Param.CURRENT_MUSIC, music)

        sendBroadcastWithPermission(intent)
    }

    private fun sendPlayState(flag: Boolean) {
        val intent = Intent(Constant.BroadcastAction.PLAY_STATE)
        intent.putExtra(Constant.Param.FLAG, flag)

        sendBroadcastWithPermission(intent)
    }

    /**
     * 限制广播发送范围
     */
    private fun sendBroadcastWithPermission(intent: Intent) {
        val permission = "$packageName.permission.${Constant.APP_NAME}"
        sendBroadcast(intent, permission)
    }

    private fun buildNotiBroadcastPendingIntent(requestCode: Int, broadcastAction: String): PendingIntent {
        val intent = Intent(broadcastAction)
        return PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private inner class MusicManagerImpl : MusicManager.Stub() {
        override fun musics(musics: MutableList<MusicAidl>) {
            internalUpdateMusics(musics)
        }

        override fun updatePlayModel(playModel: Int) {
            internalUpdatePlayModel(playModel)
        }

        override fun applyPlayState() {
            sendPlayState(mediaPlayer.isPlaying())
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
            val flag = mediaPlayer.play()
            if (flag) {
                changeMusicNotification()
                sendPlayState(true)
            }
        }

        override fun pause() {
            val flag = mediaPlayer.pause()
            if (flag) {
                changeMusicNotification()
                sendPlayState(false)
            }
        }
    }

    private fun internalUpdatePlayModel(playModel: Int) {
        mediaPlayer.updatePlayModel(playModel)
    }

    private fun internalUpdateMusics(musics: List<MusicAidl>) {
        mediaPlayer.updateMusics(musics)
    }
}