package com.hardrubic.music.di.module

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.CommonMusicListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CommonMusicListFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeCommonMusicListFragment(): CommonMusicListFragment

}