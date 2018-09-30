#include <jni.h>
#include <string>
#include <stdio.h>
#include <android/log.h>
#include "openssl/crypto.h"
#include "openssl/md5.h"
#include "openssl/evp.h"
#include "openssl/rsa.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "mydebug",__VA_ARGS__)

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_hardrubic_music_biz_encrypt_EncryptParamBuilder_aesEncrypt(JNIEnv *env, jobject instance, jstring text_, jstring key_,
                                                        jstring iv_) {
    const char *in = env->GetStringUTFChars(text_, 0);
    const char *key = env->GetStringUTFChars(key_, 0);
    const char *iv = env->GetStringUTFChars(iv_, 0);
    env->ReleaseStringUTFChars(text_, in);
    env->ReleaseStringUTFChars(key_, key);
    env->ReleaseStringUTFChars(iv_, iv);

    size_t inlen = strlen(in);
    LOGD("param(%s %s %s %d)", in, key, iv, inlen);

    char out[128];
    memset((char *) out, 0, 128);
    int len = 0;
    int outlen = 0;

    EVP_CIPHER_CTX *ctx;
    ctx = EVP_CIPHER_CTX_new();
    EVP_EncryptInit_ex(ctx, EVP_aes_128_cbc(), NULL, (unsigned char *) key, (unsigned char *) iv);
    EVP_EncryptUpdate(ctx, (unsigned char *) out, &len, (unsigned char *) in, inlen);
    outlen = len;
    EVP_EncryptFinal_ex(ctx, (unsigned char *) out + len, &len);
    outlen += len;
    EVP_CIPHER_CTX_free(ctx);

    jbyteArray j_byte_array = env->NewByteArray(outlen);
    env->SetByteArrayRegion(j_byte_array, 0, outlen, (jbyte *) out);


    return j_byte_array;
}