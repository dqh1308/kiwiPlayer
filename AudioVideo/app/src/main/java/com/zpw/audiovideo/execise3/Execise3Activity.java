package com.zpw.audiovideo.execise3;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.zpw.audiovideo.R;

/**
 * Created by zpw on 2018/5/23.
 */

public class Execise3Activity extends AppCompatActivity {
    private static final String TAG = "Execise3Activity";

    private Camera mCamera;
//    private CameraPreview mPreview;
    private CameraPreview2 mPreview;
    private boolean isPreviewInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise3);
        new InitCameraThread().start();
    }

    @Override
    protected void onResume() {
        if (null == mCamera) {
            for (;;) {
                if (safeCameraOpen() && isPreviewInit) {
                    mPreview.setCamera(mCamera); // 重新获取camera操作权
                    break;
                } else {
                    Log.e(TAG, "无法操作camera");
                    SystemClock.sleep(100);
                }
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    private boolean safeCameraOpen() {
        boolean qOpened = false;
        try {
            releaseCamera();
            mCamera = Camera.open();
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(TAG, "failed to open Camera");
            e.printStackTrace();
        }
        Log.e(TAG, "failed to open Camera -> " + qOpened);
        return qOpened;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private class InitCameraThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (;;) {
                if (safeCameraOpen()) {
                    Log.d(TAG, "开启摄像头");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        mPreview = new CameraPreview(Execise3Activity.this, mCamera);
                            mPreview = new CameraPreview2(Execise3Activity.this, mCamera);
                            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                            preview.addView(mPreview);
                            isPreviewInit = true;
                        }
                    });
                    break;
                } else {
                    SystemClock.sleep(100);
                }
            }
        }
    }
}
