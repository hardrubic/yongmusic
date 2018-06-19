package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.MusicControlViewModel
import dagger.Component

@Component
interface MusicControlViewModelComponent {

    fun inject(viewModel: MusicControlViewModel)
}
