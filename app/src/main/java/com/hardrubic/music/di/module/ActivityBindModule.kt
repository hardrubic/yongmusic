package com.hardrubic.music.di.module

import com.hardrubic.music.di.module.bindfragment.ArtistDetailActivityBindFragmentModule
import com.hardrubic.music.di.module.bindfragment.MainActivityBindFragmentModule
import com.hardrubic.music.di.module.bindfragment.PlayingActivityBindFragmentModule
import com.hardrubic.music.di.module.bindfragment.SearchActivityBindFragmentModule
import com.hardrubic.music.di.scope.ActivityScope
import com.hardrubic.music.ui.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityBindFragmentModule::class, MusicControlModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SearchActivityBindFragmentModule::class, MusicControlModule::class])
    abstract fun contributeSearchActivity(): SearchActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, CommonMusicListFragmentModule::class])
    abstract fun contributeRecentActivity(): RecentActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, CommonMusicListFragmentModule::class])
    abstract fun contributeLocalMusicActivity(): LocalMusicActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, CommonMusicListFragmentModule::class])
    abstract fun contributeCollectionDetailActivity(): CollectionDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, PlayingActivityBindFragmentModule::class])
    abstract fun contributePlayingActivity(): PlayingActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, ArtistDetailActivityBindFragmentModule::class, CommonMusicListFragmentModule::class])
    abstract fun contributeArtistDetailActivity(): ArtistDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class, CommonMusicListFragmentModule::class])
    abstract fun contributeAlbumDetailActivity(): AlbumDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MusicControlModule::class])
    abstract fun contributeSelectCollectionActivity(): SelectCollectionActivity
}
