package com.zpw.audiovideo.execise6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

/**
 * Created by zpw on 2018/5/23.
 */

public class Execise6Activity extends AppCompatActivity {
    private static final String TAG = "Execise6Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(new ImageGLSurfaceView(this)); // 加载图片
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
