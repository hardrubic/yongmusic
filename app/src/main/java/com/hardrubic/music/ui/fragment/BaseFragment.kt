package com.hardrubic.music.ui.fragment

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

open class BaseFragment : Fragment() {
    protected lateinit var mActivity: AppCompatActivity

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        AndroidSupportInjection.inject(this)
    }
}