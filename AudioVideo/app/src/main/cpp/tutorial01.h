//
// Created by zpw on 2018/5/21.
//

#ifndef AUDIOVIDEO_TUTORIAL01_H
#define AUDIOVIDEO_TUTORIAL01_H

struct AVFrame;
class tutorial01 {
public:
    void SaveFrame(AVFrame *pFrame, int width, int height, int iFrame);
    void MakingScreencaps(const char *path);
    tutorial01();
};


#endif //AUDIOVIDEO_TUTORIAL01_H
