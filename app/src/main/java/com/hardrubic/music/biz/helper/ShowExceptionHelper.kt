package com.hardrubic.music.biz.helper

import android.support.v7.app.AppCompatActivity
import com.hardrubic.music.biz.interf.DialogBtnListener
import com.hardrubic.music.ui.fragment.ExceptionDialogFragment

object ShowExceptionHelper {

    //TODO 解决异步情况下的重复显示
    @Synchronized
    fun show(activity: AppCompatActivity, throwable: Throwable, listener: DialogBtnListener? = null): ExceptionDialogFragment? {
        if (activity.isFinishing) {
            return null
        }

        val bizExceptionDialogFragment = ExceptionDialogFragment(throwable, listener)
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.add(bizExceptionDialogFragment, ExceptionDialogFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
        return bizExceptionDialogFragment
    }
}