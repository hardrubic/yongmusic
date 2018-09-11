package com.hardrubic.music.di.module.bindfragment

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.PlayingMusicMoreDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PlayingActivityBindFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributePlayingMusicMoreDialogFragment(): PlayingMusicMoreDialogFragment
}