package com.hardrubic.music.util

import android.app.ProgressDialog
import android.content.Context

import com.hardrubic.music.R

class LoadingDialogUtil private constructor() {
    private var mProgressDialog: ProgressDialog? = null

    fun dismissLoadingDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
                mProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mProgressDialog = null
        }
    }

    fun showProgressDialog(context: Context, messageResourceId: Int = R.string.loading) {
        showProgressDialog(context, context.resources.getString(messageResourceId))
    }

    private fun showProgressDialog(context: Context, message: String = "") {
        mProgressDialog = ProgressDialog.show(context, "", message, true, false)
        mProgressDialog?.setOnCancelListener { mProgressDialog = null }
    }

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance = LoadingDialogUtil()
    }
}
