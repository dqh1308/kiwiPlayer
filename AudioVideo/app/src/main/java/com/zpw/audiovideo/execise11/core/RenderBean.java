package com.zpw.audiovideo.execise11.core;


import com.zpw.audiovideo.execise11.egl.EglHelper;

public class RenderBean {

    public EglHelper egl;
    public int sourceWidth;
    public int sourceHeight;
    public int textureId;
    public boolean endFlag;

    public long timeStamp;
    public long textureTime;

    public long threadId;

}
