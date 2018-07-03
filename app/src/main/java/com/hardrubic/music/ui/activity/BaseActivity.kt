package com.hardrubic.music.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.aidl.MusicAidl
import com.hardrubic.music.biz.MusicControl
import com.hardrubic.music.biz.helper.CurrentPlayingHelper
import com.hardrubic.music.biz.interf.MusicStateListener
import com.hardrubic.music.ui.fragment.MusicControlFragment
import com.hardrubic.music.util.LogUtil
import java.lang.ref.WeakReference

open class BaseActivity : AppCompatActivity() {

    private var musicControlFragment: MusicControlFragment? = null
    private var musicServiceMonitor: MusicServiceMonitor? = null
    private val musicStateListeners = mutableListOf<MusicStateListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        musicServiceMonitor = MusicServiceMonitor(this)
        val intentFilter = IntentFilter().apply {
            addAction(Constant.BroadcastAction.PROGRESS)
            addAction(Constant.BroadcastAction.CURRENT_MUSIC)
            addAction(Constant.BroadcastAction.PLAY_STATE)
        }
        registerReceiver(musicServiceMonitor, intentFilter)

        MusicControl.instance.bindMusicService(this)
    }

    override fun onDestroy() {
        unregisterReceiver(musicServiceMonitor)
        MusicControl.instance.unbindMusicService(this)

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    protected fun showMusicControl() {
        val ft = supportFragmentManager.beginTransaction()
        if (musicControlFragment == null) {
            musicControlFragment = MusicControlFragment.newInstance()
            ft.add(R.id.fl_music_control, musicControlFragment)
        } else {
            ft.show(musicControlFragment)
        }
        ft.commit()
    }

    fun notifyProgress(progress: Int) {
        musicStateListeners.forEach {
            it.updateProgress(progress)
        }
    }

    fun notifyCurrentMusic(musicId: Long) {
        musicStateListeners.forEach {
            it.updateCurrentMusic(musicId)
        }
    }

    fun notifyPlayingState(flag: Boolean) {
        musicStateListeners.forEach {
            it.updatePlayingState(flag)
        }
    }

    private class MusicServiceMonitor() : BroadcastReceiver() {

        private lateinit var weakReference: WeakReference<BaseActivity>

        constructor(activity: BaseActivity) : this() {
            this.weakReference = WeakReference(activity)
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                Constant.BroadcastAction.PROGRESS -> {
                    val progress = intent.getIntExtra(Constant.Param.PROGRESS, -1)
                    //LogUtil.d("broadcast receive $action:$progress")
                    weakReference.get()?.notifyProgress(progress)
                }
                Constant.BroadcastAction.CURRENT_MUSIC -> {
                    val musicAidl = intent.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
                    CurrentPlayingHelper.setPlayingMusicId(musicAidl.musicId)
                    LogUtil.d("broadcast receive $action:$musicAidl")
                    weakReference.get()?.notifyCurrentMusic(musicAidl.musicId)
                }
                Constant.BroadcastAction.PLAY_STATE -> {
                    val flag = intent.getBooleanExtra(Constant.Param.FLAG, false)
                    LogUtil.d("broadcast receive $action:$flag")
                    weakReference.get()?.notifyPlayingState(flag)
                }
            //todo addAlbum recent
            }
        }
    }

    fun addMusicStateListener(listener: MusicStateListener) {
        musicStateListeners.add(listener)
    }

    fun removeMusicStateListener(listener: MusicStateListener) {
        musicStateListeners.remove(listener)
    }


}