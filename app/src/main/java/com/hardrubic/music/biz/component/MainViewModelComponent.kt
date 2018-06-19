package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.MainViewModel
import dagger.Component

@Component
interface MainViewModelComponent {

    fun inject(viewModel: MainViewModel)
}
