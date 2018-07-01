package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import com.hardrubic.music.biz.vm.CommonMusicListViewModel
import dagger.Component

@Component
interface CommonMusicListViewModelComponent {

    fun inject(viewModel: CommonMusicListViewModel)
}
