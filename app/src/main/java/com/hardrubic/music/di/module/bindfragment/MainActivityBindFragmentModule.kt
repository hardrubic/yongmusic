package com.hardrubic.music.di.module.bindfragment

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.MainListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityBindFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeMainListFragment(): MainListFragment
}