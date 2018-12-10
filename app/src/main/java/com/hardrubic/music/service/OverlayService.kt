package com.hardrubic.music.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.TextView
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import com.hardrubic.music.entity.aidl.MusicAidl
import com.hardrubic.music.ui.activity.MainActivity
import com.hardrubic.music.ui.activity.PlayingActivity


class OverlayService : Service() {

    private lateinit var customGestureDetector: GestureDetector
    private lateinit var windowManager: WindowManager
    private var overlayLayout: View? = null
    private var overlayLayoutParams: WindowManager.LayoutParams? = null
    private lateinit var messenger: Messenger
    private var playing = false

    override fun onCreate() {
        super.onCreate()

        customGestureDetector = GestureDetector(this, CustomSimpleGestureDetector())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        messenger = Messenger(ReceiveMsgHandler())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showWindow()

        val playingMusic = intent?.getParcelableExtra<MusicAidl>(Constant.Param.CURRENT_MUSIC)
        if (playingMusic != null) {
            updateShow(playingMusic.name)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }

    override fun onDestroy() {
        overlayLayout?.let {
            windowManager.removeView(it)
        }

        super.onDestroy()
    }

    private fun showWindow() {
        if (Settings.canDrawOverlays(this)) {
            overlayLayoutParams = WindowManager.LayoutParams().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    this.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                this.width = WindowManager.LayoutParams.WRAP_CONTENT
                this.height = WindowManager.LayoutParams.WRAP_CONTENT
                this.x = 0
                this.y = 0
                this.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }

            overlayLayout = LayoutInflater.from(this).inflate(R.layout.layout_overlay_window, null)
            overlayLayout!!.setOnTouchListener(OverlayTouchListener())
            windowManager.addView(overlayLayout, overlayLayoutParams)
        }
    }

    private fun updateShow(name: String) {
        val tv = overlayLayout?.findViewById<TextView>(R.id.tv_name)
        if (name.isEmpty()) {
            playing = false
            tv?.text = getString(R.string.no_music)
        } else {
            playing = true
            tv?.text = name
        }
    }

    private inner class OverlayTouchListener : View.OnTouchListener {
        private var lastTouchX = 0F
        private var lastTouchY = 0F

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            //拖动处理
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    overlayLayoutParams!!.x += (event.rawX - lastTouchX).toInt()
                    overlayLayoutParams!!.y += (event.rawY - lastTouchY).toInt()
                    windowManager.updateViewLayout(overlayLayout, overlayLayoutParams)
                    lastTouchX = event.rawX
                    lastTouchY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                }
            }

            //点击处理
            return customGestureDetector.onTouchEvent(event)
        }
    }

    private inner class CustomSimpleGestureDetector : GestureDetector.SimpleOnGestureListener() {
        //TODO 回到应用进程显示
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (playing) {
                startActivity(Intent(this@OverlayService, PlayingActivity::class.java))
            } else {
                startActivity(Intent(this@OverlayService, MainActivity::class.java))
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stopSelf()
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            MusicServiceControl.runInMusicService(this@OverlayService) {
                it.applyNext()
            }
        }
    }

    private inner class ReceiveMsgHandler : Handler() {
        override fun handleMessage(fromMsg: Message) {

            when (fromMsg.what) {
                Constant.HandlerMsg.MUSIC_NAME -> {
                    val name = fromMsg.obj as String
                    updateShow(name)
                }
            }

            super.handleMessage(fromMsg)
        }
    }
}