package com.zpw.audiovideo.execise6;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.io.IOException;

/**
 * 展示图片的 GLSurfaceView
 */
public class ImageGLSurfaceView extends BaseGLSurfaceView {

    public ImageGLSurfaceView(Context context) throws IOException {
        super(context);

        setRenderer(new ImageRenderer(context));  // 展示图片渲染器

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        requestRender();
    }
}
