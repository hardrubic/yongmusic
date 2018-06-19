package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.PlayingViewModel
import dagger.Component

@Component
interface PlayingViewModelComponent {

    fun inject(viewModel: PlayingViewModel)
}
