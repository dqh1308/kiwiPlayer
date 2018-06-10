package com.zpw.audiovideo.execise1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zpw on 2018/5/23.
 */

public class CustomView extends View {
    Paint paint = new Paint();
    Bitmap bitmap;

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        String testImgPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/test.jpg";
        bitmap = BitmapFactory.decodeFile(testImgPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }
}
