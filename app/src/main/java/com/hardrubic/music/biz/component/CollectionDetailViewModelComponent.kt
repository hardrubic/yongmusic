package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.CollectionDetailViewModel
import dagger.Component

@Component
interface CollectionDetailViewModelComponent {

    fun inject(viewModel: CollectionDetailViewModel)
}
