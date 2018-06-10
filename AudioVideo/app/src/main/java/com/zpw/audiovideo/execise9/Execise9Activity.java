package com.zpw.audiovideo.execise9;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zpw.audiovideo.R;

public class Execise9Activity extends AppCompatActivity {
    private static final String TAG = "Execise9Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise9);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, VideoRecordFragment.newInstance()).commit();
    }

}
