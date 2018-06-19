package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.SearchViewModel
import dagger.Component

@Component
interface SearchViewModelComponent {

    fun inject(viewModel: SearchViewModel)
}
