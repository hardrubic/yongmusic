package com.hardrubic.music.ui.fragment

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

open class BaseFragment : Fragment() {
    protected lateinit var mActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }
}