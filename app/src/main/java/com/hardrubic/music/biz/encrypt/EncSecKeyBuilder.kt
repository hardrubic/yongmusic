package com.hardrubic.music.biz.encrypt

import com.hardrubic.music.util.ArrayUtil.btoi

object EncSecKeyBuilder {
    private val modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7"
    private val exponent = "010001"
    private val rsaPublicKey = RSAUtil.createRsaPublicKey(modulus, exponent)

    fun get(randomText: String): String {
        val reverseText = randomText.reversed()

        var encryptByteResult = RSAUtil.encryptWithPublicKyNoPadding(reverseText.toByteArray(), rsaPublicKey)
        encryptByteResult = fillZero(encryptByteResult)
        var intArray = btoi(encryptByteResult)!!.reversedArray()
        return intArrayToHex(intArray)
    }

    private fun fillZero(input: ByteArray): ByteArray {
        val result = ByteArray(input.size * 2)
        var i = 0
        while (i < input.size) {
            result[i * 2 + 0] = '\u0000'.toByte()
            result[i * 2 + 1] = '\u0000'.toByte()
            result[i * 2 + 2] = input[i]
            result[i * 2 + 3] = input[i + 1]
            i += 2
        }
        return result
    }

    private fun intToHex(value: Int): String {
        var result = Integer.toHexString(value)
        while (result.length < 4) {
            result = "0$result"
        }
        return result
    }

    private fun intArrayToHex(inputArray: IntArray): String {
        var highIndex = inputArray.size - 1
        var b = ""
        while (highIndex > -1) {
            val value = inputArray[highIndex]
            b += intToHex(value)
            --highIndex
        }
        return b
    }
}
