package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.ArtistDetailViewModel
import dagger.Component

@Component
interface ArtistDetailViewModelComponent {

    fun inject(viewModel: ArtistDetailViewModel)
}
