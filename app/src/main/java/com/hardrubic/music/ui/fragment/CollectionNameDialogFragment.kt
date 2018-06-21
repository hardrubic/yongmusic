package com.hardrubic.music.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import com.hardrubic.music.R

class CollectionNameDialogFragment : AppCompatDialogFragment() {

    var listener: OnFinishCollectionNameListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = AppCompatEditText(activity)
        editText.hint = getString(R.string.hint_collection_name)

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.add_collection)
        builder.setView(editText)
        builder.setPositiveButton(R.string.ok) { dialog, which ->
            finishInput(editText.text.trim().toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }

        return builder.create()
    }

    private fun finishInput(name: String) {
        if (!TextUtils.isEmpty(name)) {
            listener?.onFinish(name)
        }
    }

    companion object {
        public val TAG = CollectionNameDialogFragment::class.java.simpleName
    }
}

interface OnFinishCollectionNameListener {
    fun onFinish(name: String)
}