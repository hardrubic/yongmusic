package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

open class BaseFragment : Fragment() {
    protected lateinit var mActivity: FragmentActivity

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
        AndroidSupportInjection.inject(this)
    }
}