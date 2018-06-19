package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.RecentViewModel
import dagger.Component

@Component
interface RecentViewModelComponent {

    fun inject(viewModel: RecentViewModel)
}
