package com.hardrubic.music.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.hardrubic.music.R

class ProgressDialogFragment : AppCompatDialogFragment() {

    private lateinit var pb_progress: ProgressBar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_progress_dialog, null)
        pb_progress = view.findViewById(R.id.pb_progress)

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.downloading)
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return dialog
    }

    fun refreshProgress(progress: Int, max: Int) {
        pb_progress.max = max
        pb_progress.progress = progress
    }

    companion object {
        public val TAG = ProgressDialogFragment::class.java.simpleName
    }
}