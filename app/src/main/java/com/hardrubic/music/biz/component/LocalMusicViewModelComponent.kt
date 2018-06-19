package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.LocalMusicViewModel
import dagger.Component

@Component
interface LocalMusicViewModelComponent {

    fun inject(viewModel: LocalMusicViewModel)
}
