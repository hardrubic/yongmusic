package com.hardrubic.music.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.hardrubic.music.R
import com.hardrubic.music.biz.LoginInfo
import com.hardrubic.music.network.HttpService
import com.hardrubic.music.network.response.LoginResponse
import com.hardrubic.music.util.MD5Util
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        btn_login_in.setOnClickListener { attemptLogin() }
    }


    private fun attemptLogin() {
        tv_phone.error = null
        tv_password.error = null

        val account = tv_phone.text.toString()
        val password = tv_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tv_password.error = getString(R.string.error_invalid_password)
            focusView = tv_password
            cancel = true
        }

        if (TextUtils.isEmpty(account)) {
            tv_phone.error = getString(R.string.error_field_required)
            focusView = tv_phone
            cancel = true
        } else if (!isPhoneValid(account)) {
            tv_phone.error = getString(R.string.error_invalid_account)
            focusView = tv_phone
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
            return
        }

        val passwordMd5 = MD5Util.getDigest(password)
        HttpService.instance.applyLogin(account, passwordMd5)
                .subscribe(Consumer<LoginResponse> {
                    val loginId = it.account!!.id
                    val loginName = it.profile!!.nickname
                    LoginInfo.updateUser(loginId, loginName)
                    finish()
                }, Consumer {
                    Snackbar.make(btn_login_in, R.string.error_login_fail, Snackbar.LENGTH_SHORT).show()
                    it.printStackTrace()
                })
    }

    private fun isPhoneValid(phone: String): Boolean {
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }
}
