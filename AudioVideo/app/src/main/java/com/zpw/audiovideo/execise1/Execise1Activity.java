package com.zpw.audiovideo.execise1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zpw.audiovideo.R;

public class Execise1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise1);

//        ImageView imgShowPicture = (ImageView) findViewById(R.id.img_show_picture);
//        String testImgPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/test.jpg";
//        Bitmap bitmap = BitmapFactory.decodeFile(testImgPath);
//        imgShowPicture.setImageBitmap(bitmap);

//        SurfaceView surfaceShowPicture = (SurfaceView) findViewById(R.id.surface_show_picture);
//        surfaceShowPicture.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                if (holder == null) return;
//
//                Paint paint = new Paint();
//                paint.setAntiAlias(true);
//                paint.setStyle(Paint.Style.STROKE);
//
//                String testImgPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/test.jpg";
//                Bitmap bitmap = BitmapFactory.decodeFile(testImgPath);
//                Canvas canvas = holder.lockCanvas();
//                canvas.drawBitmap(bitmap, 0, 0, paint);
//                holder.unlockCanvasAndPost(canvas);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });


    }

}
