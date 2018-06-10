package com.zpw.audiovideo.execise7;

import android.util.Log;

/**
 * Created by zpw on 2018/5/24.
 */

public class LogUtils {
    public final static String TAG = "eric";

    public static void v(String content) {
        Log.v(TAG, content);
    }
    public static void d(String content) {
        Log.d(TAG, content);
    }
    public static void e(String content) {
        Log.e(TAG, content);
    }

    public static void w(String content) {
        Log.w(TAG, content);
    }
}
