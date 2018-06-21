package com.hardrubic.music.biz.component

import com.hardrubic.music.biz.vm.SelectCollectionViewModel
import dagger.Component

@Component
interface SelectCollectionViewModelComponent {

    fun inject(viewModel: SelectCollectionViewModel)
}
