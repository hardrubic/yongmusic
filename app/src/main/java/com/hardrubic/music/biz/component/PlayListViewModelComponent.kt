package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.PlayListViewModel
import dagger.Component

@Component
interface PlayListViewModelComponent {

    fun inject(viewModel: PlayListViewModel)
}
