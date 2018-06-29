package com.hardrubic.music.biz

import com.hardrubic.music.Constant
import com.hardrubic.music.util.PreferencesUtil

object LoginInfo {

    var isLogin: Boolean
        get() = PreferencesUtil.instance.getBoolean(Constant.SpKey.IS_LOGIN, false)
        private set(isUserLogin) {
            PreferencesUtil.instance.putBoolean(Constant.SpKey.IS_LOGIN, isUserLogin)
        }

    var loginId: Long?
        get() = PreferencesUtil.instance.getLong(Constant.SpKey.LOGIN_ID)
        set(userId) {
            PreferencesUtil.instance.putLong(Constant.SpKey.LOGIN_ID, userId!!)
        }

    var loginName: String
        get() = PreferencesUtil.instance.getString(Constant.SpKey.LOGIN_NAME, "")
        set(name) {
            PreferencesUtil.instance.putString(Constant.SpKey.LOGIN_NAME, name)
        }

    /**
     * 登录成功后调用
     */
    fun updateUser(id: Long, name: String) {
        this.isLogin = true
        this.loginId = id
        this.loginName = name
    }

    /**
     * 登出后调用
     */
    fun clearUser() {
        this.isLogin = false
    }
}
