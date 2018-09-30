package com.hardrubic.music.biz.encrypt

import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

object RSAUtil {

    private val ALGORITHM = "RSA"

    fun createRsaPublicKey(modulus: String, exponent: String): RSAPublicKey {
        val radix = 16
        val pubKeySpec = RSAPublicKeySpec(BigInteger(modulus, radix), BigInteger(exponent, radix))
        val pubKey = KeyFactory.getInstance(ALGORITHM).generatePublic(pubKeySpec) as RSAPublicKey
        Cipher.getInstance(ALGORITHM).init(Cipher.ENCRYPT_MODE, pubKey)
        return pubKey
    }

    fun encryptWithPublicKyNoPadding(content: ByteArray, publicKey: RSAPublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/NOPADDING")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(content)
    }
}