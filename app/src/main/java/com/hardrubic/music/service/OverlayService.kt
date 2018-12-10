package com.hardrubic.music.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import com.hardrubic.music.R
import com.hardrubic.music.ui.activity.MainActivity


class OverlayService : Service() {

    private lateinit var customGestureDetector: GestureDetector
    private lateinit var windowManager: WindowManager
    private var overlayLayout: View? = null
    private var overlayLayoutParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()

        customGestureDetector = GestureDetector(this, CustomSimpleGestureDetector())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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

    private inner class OverlayTouchListener : View.OnTouchListener {
        private var lastTouchX = 0F
        private var lastTouchY = 0F

        //拖动处理
        override fun onTouch(v: View, event: MotionEvent): Boolean {
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
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            startActivity(Intent(this@OverlayService, MainActivity::class.java))
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stopSelf()
            return false
        }
    }
}