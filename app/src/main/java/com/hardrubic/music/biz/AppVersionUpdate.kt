package com.hardrubic.music.biz

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.hardrubic.music.R
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean

object AppVersionUpdate {

    fun registerCheckUpdate(activity: Activity, isSilent: Boolean) {
        val builder = PgyUpdateManager.Builder()
        builder.setUserCanRetry(true)
        builder.setDeleteHistroyApk(true)
        builder.setUpdateManagerListener(object : UpdateManagerListener {
            override fun onNoUpdateAvailable() {
                if (!isSilent) {
                    Toast.makeText(activity, activity.getString(R.string.hint_no_new_version), Toast.LENGTH_LONG).show()
                }
            }

            override fun onUpdateAvailable(appBean: AppBean) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("${activity.getString(R.string.version_update)}(${appBean.versionName})")
                builder.setMessage(appBean.releaseNote)
                builder.setPositiveButton(R.string.ok) { dialog, which -> PgyUpdateManager.downLoadApk(appBean.downloadURL) }
                builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                builder.create().show()
            }

            override fun checkUpdateFailed(e: Exception) {
                if (!isSilent) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        //builder.setDownloadFileListener() //自定义apk下载
        builder.register()
    }

    fun unregisterCheckUpdate() {
        PgyUpdateManager.unRegister()
    }

}
