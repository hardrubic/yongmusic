package com.hardrubic.music.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import com.hardrubic.music.R
import com.hardrubic.music.biz.interf.DialogBtnListener

@SuppressLint("ValidFragment")
class ExceptionDialogFragment(private val throwable: Throwable, private val listener: DialogBtnListener?) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.hint)
        builder.setMessage(throwable.message)
        if (listener != null) {
            builder.setPositiveButton(R.string.retry) { dialog, which -> listener.onClickOkListener(dialog) }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> listener?.onClickCancelListener(dialog) }

        val alertDialog = builder.create()
        alertDialog.show()

        return alertDialog
    }

    companion object {
        val TAG = ExceptionDialogFragment::class.java.simpleName
    }
}
