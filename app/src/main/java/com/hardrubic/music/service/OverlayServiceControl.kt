package com.hardrubic.music.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.hardrubic.music.Constant
import com.hardrubic.music.util.LogUtil

object OverlayServiceControl {

    private var serviceConnection: ServiceConnection? = null

    fun changeMusicName(context: Context, name: String) {
        register(context) {
            val msg = Message.obtain()
            msg.what = Constant.HandlerMsg.MUSIC_NAME
            msg.obj = name
            it.send(msg)

            unregister(context)
        }
    }

    private fun register(context: Context, connectCallback: ((messager: Messenger) -> Unit)? = null) {
        serviceConnection = OverlayServiceConnection(connectCallback)

        val intent = Intent(context, OverlayService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)    //TODO 存在才绑定
    }

    private fun unregister(context: Context) {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection)
            serviceConnection = null
        }
    }

    private class OverlayServiceConnection(val connectCallback: ((messager: Messenger) -> Unit)?) : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.d("bind overlay service")
            val messenger = Messenger(service)
            connectCallback?.invoke(messenger)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtil.d("unbind overlay service")
        }
    }
}