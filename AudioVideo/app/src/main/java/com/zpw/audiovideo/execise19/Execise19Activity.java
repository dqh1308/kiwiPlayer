/*
 *
 * PlayerActivity.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.zpw.audiovideo.execise19;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;

import com.zpw.audiovideo.R;

/**
 * Description:
 */
public class Execise19Activity extends Activity {

    private EditText mEditAddress;
    private SurfaceView mPlayerView;
    private MPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise19);
        initView();
        initPlayer();
    }

    private void initView(){
        mEditAddress= (EditText) findViewById(R.id.mEditAddress);
        mPlayerView= (SurfaceView) findViewById(R.id.mPlayerView);
    }

    private void initPlayer(){
        player=new MPlayer();
        player.setDisplay(new MinimalDisplay(mPlayerView));
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.mPlay:
                String mUrl=mEditAddress.getText().toString();
                if(mUrl.length()>0){
                    try {
                        player.setSource(mUrl);
                        player.play();
                    } catch (MPlayerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.mPlayerView:
                if(player.isPlaying()){
                    player.pause();
                }else{
                    try {
                        player.play();
                    } catch (MPlayerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.mType:
                player.setCrop(!player.isCrop());
                break;
        }
    }

}
