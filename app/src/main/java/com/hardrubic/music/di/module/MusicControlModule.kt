package com.hardrubic.music.di.module

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.MusicControlFragment
import com.hardrubic.music.ui.fragment.PlayListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MusicControlModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeMusicControlFragment(): MusicControlFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributePlayListFragment(): PlayListFragment

}