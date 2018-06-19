package com.hardrubic.music.network


import android.text.TextUtils
import okhttp3.FormBody
import okhttp3.Request

object HttpUtil {

    /**
     * 获取Post请求的请求参数
     */
    fun buildHttpPostParam(request: Request): String {
        val results = mutableListOf<String>()
        val requestBody = request.body()
        if (request.method().toUpperCase() == "POST") {
            if (requestBody is FormBody) {
                val formBody = requestBody as FormBody
                for (i in 0 until formBody.size()) {
                    results.add("${formBody.name(i)}=${formBody.value(i)}")
                }
            }
        }


        if (results.isNotEmpty()) {
            return TextUtils.join("$", results)
        }
        return ""
    }
}
