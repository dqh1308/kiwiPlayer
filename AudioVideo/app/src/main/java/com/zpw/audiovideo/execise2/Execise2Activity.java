package com.zpw.audiovideo.execise2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zpw.audiovideo.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zpw on 2018/5/23.
 */

public class Execise2Activity extends AppCompatActivity {
    // 音频源：音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 8000;
    // 音频通道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    // 音频格式：PCM编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区大小：缓冲区字节大小
    private int bufferSizeInBytes = 0;
    // 录音对象
    private AudioRecord audioRecord;
    // 播放对象
    private AudioTrack audioTrack;

    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);
        Button record = (Button) findViewById(R.id.bun_test_record);
        Button play = (Button) findViewById(R.id.bun_test_play);
        Button play2 = (Button) findViewById(R.id.bun_test_play2);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAudioRecord();
                final byte[] data = new byte[bufferSizeInBytes];
                audioRecord.startRecording();
                isRecording = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream os = null;
                        int read;
                        try {
                            os = new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.wav");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (null != os) {
                            while (isRecording) {
                                read = audioRecord.read(data, 0, bufferSizeInBytes);
                                // 如果读取音频数据没有出现错误，就将数据写入到文件
                                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                    try {
                                        os.write(data);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileInputStream is = null;
                        try {
                            is = new FileInputStream(Environment.getExternalStorageDirectory() + "/test.wav");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream out = new ByteArrayOutputStream(264848);
                        try {
                            for (int i = 0; (i = is.read()) != -1;) {
                                out.write(i);
                            }
                            byte[] audioData = out.toByteArray();
                            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_RATE,
                                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                                    audioData.length, AudioTrack.MODE_STATIC);
                            audioTrack.write(audioData, 0, audioData.length);
                            audioTrack.play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_STEREO, AUDIO_ENCODING) * 1;
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        AUDIO_SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_STEREO, AUDIO_ENCODING, bufferSize,
                        AudioTrack.MODE_STREAM);
                FileInputStream is = null;
                try {
                    is = new FileInputStream(Environment.getExternalStorageDirectory() + "/test.wav");
                    audioTrack.play();
                    byte[] aByteBuffer = new byte[bufferSize];
                    while (is.read(aByteBuffer) >= 0) {
                        audioTrack.write(aByteBuffer, 0, aByteBuffer.length);
                    }
                    audioTrack.stop();
                    audioTrack.release();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        PCMFileToWAVFile.pcm2wav(AUDIO_SAMPLE_RATE, AUDIO_ENCODING, AUDIO_CHANNEL, 0, Environment.getExternalStorageDirectory() + "/test.wav", Environment.getExternalStorageDirectory() + "/test2.wav");
    }

    private void createAudioRecord() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (null != audioTrack) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
}
