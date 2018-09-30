package com.hardrubic.music.biz.encrypt;

import android.util.Base64;
import com.google.gson.Gson;
import java.util.HashMap;

public class EncryptParamBuilder {
    static {
        System.loadLibrary("libencrypt");
    }

    public static HashMap<String, String> encrypt(HashMap<String,String> param) {
        String paramStr = new Gson().toJson(param);
        String random16Str = buildRandomStr(16);

        HashMap<String, String> encryptResult = new HashMap<>();
        encryptResult.put("params", buildParamStr(paramStr, random16Str));
        encryptResult.put("encSecKey", EncSecKeyBuilder.INSTANCE.get(random16Str));
        return encryptResult;
    }

    private static String buildParamStr(String text, String randomStr) {
        String IV = "0102030405060708";
        String ENCRYPT_FIRST_KEY = "0CoJUm6Qyw8W8jud";
        String tmpResult = Base64.encodeToString(aesEncrypt(text, ENCRYPT_FIRST_KEY, IV), Base64.DEFAULT);
        return Base64.encodeToString(aesEncrypt(tmpResult, randomStr, IV), Base64.DEFAULT);
    }

    private static String buildRandomStr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double e = Math.random() * i;
            e = Math.floor(e);
            result.append(chars.charAt((int) e));
        }
        return result.toString();
    }

    public static native byte[] aesEncrypt(String text, String key, String iv);
}
