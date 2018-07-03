package com.hardrubic.music.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hardrubic.music.Constant
import com.hardrubic.music.R
import kotlinx.android.synthetic.main.fragment_artist_desc.*

class ArtistDescFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_artist_desc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_desc.text = arguments?.getString(Constant.Param.NAME)
    }
}