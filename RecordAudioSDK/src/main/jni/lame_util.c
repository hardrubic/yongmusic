#include "mp3lame-3.99.5/include/lame.h"
#include <stdio.h>
#include <jni.h>
#include <assert.h>

#define JNIREG_CLASS "com/hardrubic/sdk/recordaudio/record/LameUtil"

static lame_global_flags *lame = NULL;

void applyInit(JNIEnv *env, jclass cls, jint inSamplerate, jint inChannel, jint outSamplerate, jint outBitrate,
               jint quality) {
    if (lame != NULL) {
        lame_close(lame);
        lame = NULL;
    }
    lame = lame_init();
    lame_set_in_samplerate(lame, inSamplerate);
    lame_set_num_channels(lame, inChannel);//输入流的声道
    lame_set_out_samplerate(lame, outSamplerate);
    lame_set_brate(lame, outBitrate);
    lame_set_quality(lame, quality);
    lame_init_params(lame);
}

jint applyEncode(JNIEnv *env, jclass cls, jshortArray buffer_l, jshortArray buffer_r,
                 jint samples, jbyteArray mp3buf) {
    jshort *j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);

    jshort *j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);

    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_buffer(lame, j_buffer_l, j_buffer_r,
                                    samples, j_mp3buf, mp3buf_size);

    (*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
    (*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

jint applyFlush(JNIEnv *env, jclass cls, jbyteArray mp3buf) {
    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_flush(lame, j_mp3buf, mp3buf_size);

    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

void applyClose(JNIEnv *env, jclass cls) {
    lame_close(lame);
    lame = NULL;
}

// 定义一个JNINativeMethod数组，描述Java和native函数的对应关系
static JNINativeMethod gMethods[] = {
        {"init",   "(IIIII)V",   (void *) applyInit},
        {"encode", "([S[SI[B)I", (void *) applyEncode},
        {"flush",  "([B)I",      (void *) applyFlush},
        {"close",  "()V",          (void *) applyClose}
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods, sizeof(gMethods) / sizeof(gMethods[0]))) {
        return -1;
    }

    result = JNI_VERSION_1_4;
    return result;
}
