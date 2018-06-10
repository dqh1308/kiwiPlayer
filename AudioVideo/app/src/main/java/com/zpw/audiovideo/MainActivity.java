package com.zpw.audiovideo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

//        MakingScreencaps(Environment.getExternalStorageDirectory()+"/video.mpg");

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] pix = new int[width * height];
//        bitmap.getPixels(pix, 0, width, 0, 0, width, height);
//        int[] resultPixes = gray(pix, width, height);
//        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        resultBitmap.setPixels(resultPixes, 0, width, 0, 0, width, height);
//
//        ImageView imgTestOpencv = (ImageView) findViewById(R.id.img_test_opencv);
//        imgTestOpencv.setImageBitmap(resultBitmap);

    }

    //截图
    public native void MakingScreencaps(String path);

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    //OpenCV 灰度
    public native int[] gray(int[] pix, int width, int height);


}
