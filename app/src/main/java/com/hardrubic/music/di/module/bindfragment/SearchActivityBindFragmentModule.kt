package com.hardrubic.music.di.module.bindfragment

import com.hardrubic.music.di.scope.FragmentScope
import com.hardrubic.music.ui.fragment.search.SearchAlbumListFragment
import com.hardrubic.music.ui.fragment.search.SearchArtistListFragment
import com.hardrubic.music.ui.fragment.search.SearchMusicListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
public abstract class SearchActivityBindFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    public abstract fun contributeSearchMusicListFragment(): SearchMusicListFragment

    @FragmentScope
    @ContributesAndroidInjector
    public abstract fun contributeSearchArtistListFragment(): SearchArtistListFragment

    @FragmentScope
    @ContributesAndroidInjector
    public abstract fun contributeSearchAlbumListFragment(): SearchAlbumListFragment
}