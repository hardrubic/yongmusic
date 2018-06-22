package com.hardrubic.music.biz.helper

import android.support.v4.app.FragmentActivity
import com.hardrubic.music.biz.listener.DialogBtnListener
import com.hardrubic.music.ui.fragment.ExceptionDialogFragment

object ShowExceptionHelper {

    fun show(activity: FragmentActivity, throwable: Throwable, listener: DialogBtnListener) {
        if (activity.isFinishing) {
            return
        }

        val fm = activity.supportFragmentManager

        val old = fm.findFragmentByTag(ExceptionDialogFragment.TAG)
        if (old != null) {
            fm.beginTransaction().remove(old).commit()
        }

        val bizExceptionDialogFragment = ExceptionDialogFragment(throwable, listener)
        bizExceptionDialogFragment.show(fm, ExceptionDialogFragment.TAG)
        //fm.executePendingTransactions() //阻塞直到完成
    }
}