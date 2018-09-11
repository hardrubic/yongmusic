package com.hardrubic.music.di.module.bindfragment

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.ArtistAlbumFragment
import com.hardrubic.music.ui.fragment.ArtistDescFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ArtistDetailActivityBindFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeArtistAlbumFragment(): ArtistAlbumFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeArtistDescFragment(): ArtistDescFragment
}