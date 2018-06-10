#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"testff",__VA_ARGS__)

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavcodec/jni.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
}

#include<iostream>
using namespace std;

#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
using namespace cv;

#include "tutorial01.h"
#include "tutorial02.h"


extern "C"
JNIEXPORT
jint JNI_OnLoad(JavaVM *vm,void *res)
{
    av_jni_set_java_vm(vm,0);
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_zpw_audiovideo_MainActivity_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++ ";
    hello += avcodec_configuration();
    return env->NewStringUTF(hello.c_str());
}


extern "C" {
JNIEXPORT jintArray JNICALL
Java_com_zpw_audiovideo_MainActivity_gray(JNIEnv *env, jobject instance, jintArray buf, jint width, jint height) {
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL)
        return 0;

    Mat imgData(height, width, CV_8UC3, (unsigned char *)cbuf);

    uchar* ptr = imgData.ptr(0);
    for (int i = 0; i < width * height; ++i) {
        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
        //对于一个int四字节，其彩色值存储方式为：BGRA
        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        ptr[4*i+1] = grayScale;
        ptr[4*i+2] = grayScale;
        ptr[4*i+0] = grayScale;
    }
    int size = width * height;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zpw_audiovideo_MainActivity_MakingScreencaps(JNIEnv *env, jobject instance, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    tutorial01 *tutorial = new tutorial01();
    tutorial->MakingScreencaps(path);
    LOGW("MakingScreencaps\n");

    env->ReleaseStringUTFChars(path_, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zpw_audiovideo_XPlay_PlayVideoOnScreen(JNIEnv *env, jobject instance, jstring url_, jobject surface) {
    const char *url = env->GetStringUTFChars(url_, 0);

    tutorial02 *tutorial = new tutorial02();
    tutorial->PlayVideoOnScreen(env, url, surface);
    LOGW("PlayVideoOnScreen\n");

    env->ReleaseStringUTFChars(url_, url);
}