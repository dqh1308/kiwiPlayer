//
// Created by zpw on 2018/5/22.
//

#ifndef AUDIOVIDEO_TUTORIAL02_H
#define AUDIOVIDEO_TUTORIAL02_H

#include "jni.h"

class tutorial02 {
public:
    void PlayVideoOnScreen(JNIEnv *env, const char *path, jobject surface);
    tutorial02();
};


#endif //AUDIOVIDEO_TUTORIAL02_H
