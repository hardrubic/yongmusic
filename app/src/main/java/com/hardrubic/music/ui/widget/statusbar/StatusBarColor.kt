package com.hardrubic.music.ui.widget.statusbar

import android.app.Activity
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.hardrubic.music.R


object StatusBarColor {

    fun setStatusBarColor(activity: Activity, statusColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = statusColor
            //设置系统状态栏处于可见状态
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            //让view不根据系统窗口来调整自己的布局
            val mContentView = window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val mChildView = mContentView.getChildAt(0)
            if (mChildView != null) {
                mChildView!!.fitsSystemWindows = false
                ViewCompat.requestApplyInsets(mChildView)
            }
        }
    }

    fun setStatusBarLightMode(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果是6.0以上将状态栏文字改为黑色，并设置状态栏颜色
            val window = activity.window
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = color

            //fitsSystemWindow 为 false, 不预留系统栏位置.
            val mContentView = window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val mChildView = mContentView.getChildAt(0)
            if (mChildView != null) {
                mChildView!!.fitsSystemWindows = true
                ViewCompat.requestApplyInsets(mChildView)
            }
        } else {
            //5.0的暂时改为黑色
            setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.black))
        }
    }
}