package com.hardrubic.music.util

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import com.hardrubic.music.R

//todo 使用ProgressBar替代
class LoadingDialogHelper private constructor() {
    private var mProgressDialog: ProgressDialog? = null

    fun dismissLoadingDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
                mProgressDialog = null
            }
        } catch (e: IllegalArgumentException) {
            // 此时Activity已经结束
        } catch (e: Exception) {

        } finally {
            mProgressDialog = null
        }
    }

    fun showDefaultLoadingDialog(context: Context) {
        if (null == mProgressDialog) {
            showProgressDialog(context, R.string.loading, false)
        }
    }

    fun showLoadingDialog(context: Context, messageResourceId: Int, cancelable: Boolean) {
        if (null == mProgressDialog) {
            showProgressDialog(context, messageResourceId, cancelable)
        }
    }

    private fun showProgressDialog(context: Context, messageResourceId: Int, cancelable: Boolean) {
        showProgressDialog(context, context.resources.getString(messageResourceId), cancelable)
    }

    private fun showProgressDialog(context: Context, message: String, cancelable: Boolean) {
        showProgressDialog(context, "", message, cancelable)
    }

    private fun showProgressDialog(context: Context, title: String, message: String, cancelable: Boolean) {
        mProgressDialog = ProgressDialog.show(context, title, message, true, cancelable)
        mProgressDialog!!.setOnCancelListener { mProgressDialog = null }
    }

    companion object {

        private var sInstance: LoadingDialogHelper? = null

        val instance: LoadingDialogHelper
            get() {
                if (sInstance == null) {
                    sInstance = LoadingDialogHelper()
                }
                return sInstance!!
            }
    }
}
