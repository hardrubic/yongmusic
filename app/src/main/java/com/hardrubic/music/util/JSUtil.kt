package com.hardrubic.music.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.mozilla.javascript.NativeJSON
import org.mozilla.javascript.NativeJavaObject
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.ScriptableObject


object JSUtil {

    private const val ENC_TEXT = "encText"
    private const val ENC_SEC_KEY = "encSecKey"

    private var neteaseEncryptJs = ""

    fun buildEncryptParamMap(context: Context, param: HashMap<String, String>): HashMap<String, String> {
        val time1 = System.currentTimeMillis()

        if (neteaseEncryptJs.isEmpty()) {
            val inputStream = context.assets.open("netease_encrypt.js")
            neteaseEncryptJs = String(inputStream.readBytes())
        }

        val paramStr = Gson().toJson(param)
        val result = runJavaScript(neteaseEncryptJs, "myFunc", arrayOf(paramStr))
        val jsonObject = JsonParser().parse(result).asJsonObject

        val key = jsonObject.get(ENC_TEXT).toString().replace("\"", "")
        val value = jsonObject.get(ENC_SEC_KEY).toString().replace("\"", "")

        LogUtil.d("buildEncryptParamMap time:${(System.currentTimeMillis() - time1) / 1000}s")

        return hashMapOf<String, String>().apply {
            put("params", key)
            put("encSecKey", value)
        }
    }

    /**
     * 执行JS
     *
     * @param js js代码
     * @param functionName js方法名称
     * @param functionParams js方法参数
     */
    private fun runJavaScript(js: String, functionName: String, functionParams: Array<Any>): String {
        val rhino = org.mozilla.javascript.Context.enter()
        rhino.optimizationLevel = -1
        try {
            val scope = rhino.initStandardObjects()

            ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(this, scope))
            ScriptableObject.putProperty(scope, "javaLoader",
                    org.mozilla.javascript.Context.javaToJS(JSUtil::class.java.classLoader, scope))

            rhino.evaluateString(scope, js, "JS", 1, null)

            val function = scope.get(functionName, scope) as org.mozilla.javascript.Function

            val result = function.call(rhino, scope, scope, functionParams)
            return when (result) {
                is String -> result
                is NativeJavaObject -> result.getDefaultValue(String::class.java) as String
                is NativeObject -> NativeJSON.stringify(rhino, scope, result, null, null).toString()
                else -> result.toString()
            }
        } finally {
            org.mozilla.javascript.Context.exit()
        }
    }
}