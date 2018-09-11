package com.hardrubic.music.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hardrubic.music.biz.vm.*
import com.hardrubic.music.di.DaggerViewModelFactory
import com.hardrubic.music.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    /*
      注入对象到map中(key在ViewModelKey定义，是个class；value是SearchViewModel）
     */
    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecentViewModel::class)
    abstract fun bindRecentViewModel(viewModel: RecentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayingViewModel::class)
    abstract fun bindPlayingViewModel(viewModel: PlayingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionDetailViewModel::class)
    abstract fun bindCollectionDetailViewModel(viewModel: CollectionDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommonMusicListViewModel::class)
    abstract fun bindCommonMusicListViewModel(viewModel: CommonMusicListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocalMusicViewModel::class)
    abstract fun bindLocalMusicViewModel(viewModel: LocalMusicViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicControlViewModel::class)
    abstract fun bindMusicControlViewModel(viewModel: MusicControlViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayListViewModel::class)
    abstract fun bindPlayListViewModel(viewModel: PlayListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectCollectionViewModel::class)
    abstract fun bindSelectCollectionViewModel(viewModel: SelectCollectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AlbumDetailViewModel::class)
    abstract fun bindAlbumDetailViewModel(viewModel: AlbumDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailViewModel::class)
    abstract fun bindArtistDetailViewModel(viewModel: ArtistDetailViewModel): ViewModel
}