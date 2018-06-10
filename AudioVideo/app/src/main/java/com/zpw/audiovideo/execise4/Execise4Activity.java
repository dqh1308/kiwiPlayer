package com.zpw.audiovideo.execise4;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.zpw.audiovideo.R;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

/**
 * Created by zpw on 2018/5/23.
 */

public class Execise4Activity extends AppCompatActivity {
    private static final String TAG = "Execise4Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise4);
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        try {
//            extractVideo(externalStorageDirectory + "/test.mp4", externalStorageDirectory + "/test2.mp4");
//            extractAudioFromMP4(externalStorageDirectory + "/test3.mp4", externalStorageDirectory + "/test.mp4");
//            replaceAudioForMP4File(
//                    externalStorageDirectory + "/test4.mp4",
//                    externalStorageDirectory + "/test2.mp4",
//                    externalStorageDirectory + "/test3.mp4");
            AndroidAudioConverter.load(this, new ILoadCallback() {
                @Override
                public void onSuccess() {
                    // Great!
                    Log.d(TAG, "AndroidAudioConverter Init Success!");
                    translateMP3ToAAC(externalStorageDirectory + "/Take My Hand.mp3");
                }
                @Override
                public void onFailure(Exception error) {
                    // FFmpeg is not supported by device
                    Log.d(TAG, "AndroidAudioConverter Init Failure!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取视频
     *
     * @param sourceVideoPath 原始视频文件
     * @throws Exception 出错
     */
    private void extractVideo(String sourceVideoPath, String outVideoPath) throws Exception {
        MediaExtractor sourceMediaExtractor = new MediaExtractor();
        sourceMediaExtractor.setDataSource(sourceVideoPath);
        int numTracks = sourceMediaExtractor.getTrackCount();
        int sourceVideoTrackIndex = -1; // 原始视频文件视频轨道参数
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = sourceMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            Log.d(TAG, "MediaFormat: " + mime);
            if (mime.startsWith("video/")) {
                sourceMediaExtractor.selectTrack(i);
                sourceVideoTrackIndex = i;
                Log.d(TAG, "selectTrack index=" + i + "; format: " + mime);
                break;
            }
        }

        MediaMuxer outputMediaMuxer = new MediaMuxer(outVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        outputMediaMuxer.addTrack(sourceMediaExtractor.getTrackFormat(sourceVideoTrackIndex));
        outputMediaMuxer.start();

        ByteBuffer inputBuffer = ByteBuffer.allocate(1024 * 1024 * 2);// 分配的内存要尽量大一些
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int sampleSize;
        while ((sampleSize = sourceMediaExtractor.readSampleData(inputBuffer, 0)) >= 0) {
            long presentationTimeUs = sourceMediaExtractor.getSampleTime();
            info.offset = 0;
            info.size = sampleSize;
            info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            info.presentationTimeUs = presentationTimeUs;
            outputMediaMuxer.writeSampleData(sourceVideoTrackIndex, inputBuffer, info);
            sourceMediaExtractor.advance();
        }
        outputMediaMuxer.stop();
        outputMediaMuxer.release();    // 停止并释放 MediaMuxer
        sourceMediaExtractor.release();
        sourceMediaExtractor = null;   // 释放 MediaExtractor
    }

    /**
     * 提取音频
     * @param outAudioPath
     * @param sourceMP4Path
     * @throws IOException
     */
    public void extractAudioFromMP4(String outAudioPath, String sourceMP4Path) throws IOException {
        Movie movie = MovieCreator.build(sourceMP4Path);
        List<Track> audioTracks = new ArrayList<>();
        for (Track t : movie.getTracks()) {
            if (t.getHandler().equals("soun")) {
                audioTracks.add(t);
            }
        }
        Movie result = new Movie();
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(outAudioPath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }

    /**
     * @param outputVideoFilePath 输出视频文件路径
     * @param videoProviderPath   提供视频的MP4文件 时长以此为准
     * @param audioProviderPath   提供音频的文件
     * @throws Exception 运行异常  例如读写文件异常
     */
    public static void replaceAudioForMP4File(String outputVideoFilePath, String videoProviderPath,
                                              String audioProviderPath) throws Exception {
        MediaMuxer mediaMuxer = new MediaMuxer(outputVideoFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        // 视频 MediaExtractor
        MediaExtractor mVideoExtractor = new MediaExtractor();
        mVideoExtractor.setDataSource(videoProviderPath);
        int videoTrackIndex = -1;

        for (int i = 0; i < mVideoExtractor.getTrackCount(); i++) {
            MediaFormat format = mVideoExtractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                mVideoExtractor.selectTrack(i);
                videoTrackIndex = mediaMuxer.addTrack(format);
                Log.d(TAG, "Video: format:" + format);
                break;
            }
        }
        // 音频 MediaExtractor
        MediaExtractor audioExtractor = new MediaExtractor();
        audioExtractor.setDataSource(audioProviderPath);
        int audioTrackIndex = -1;
        for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
            MediaFormat format = audioExtractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioExtractor.selectTrack(i);
                audioTrackIndex = mediaMuxer.addTrack(format);
                Log.d(TAG, "Audio: format:" + format);
                break;
            }
        }
        mediaMuxer.start(); // 添加完所有轨道后start

        long videoEndPreTimeUs = 0;
        if (-1 != videoTrackIndex) {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info.presentationTimeUs = 0;
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int sampleSize;
            while ((sampleSize = mVideoExtractor.readSampleData(buffer, 0)) >= 0) {
                info.offset = 0;
                info.size = sampleSize;
                info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                info.presentationTimeUs = mVideoExtractor.getSampleTime();
                videoEndPreTimeUs = info.presentationTimeUs;
                mediaMuxer.writeSampleData(videoTrackIndex, buffer, info);
                mVideoExtractor.advance();
            }
        }
        Log.d(TAG, "视频 videoEndPreTimeUs " + videoEndPreTimeUs);

        // 封装音频track
        if (-1 != audioTrackIndex) {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info.presentationTimeUs = 0;
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int sampleSize;
            while ((sampleSize = audioExtractor.readSampleData(buffer, 0)) >= 0 &&
                    audioExtractor.getSampleTime() <= videoEndPreTimeUs) {
                info.offset = 0;
                info.size = sampleSize;
                info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                info.presentationTimeUs = audioExtractor.getSampleTime();
                mediaMuxer.writeSampleData(audioTrackIndex, buffer, info);
                audioExtractor.advance();
            }
        }
        mVideoExtractor.release(); // 释放MediaExtractor
        audioExtractor.release();
        mediaMuxer.stop();
        mediaMuxer.release();     // 释放MediaMuxer
    }

    public void translateMP3ToAAC(String sourceMP3Path) {
        Log.d(TAG, "转换开始 " + sourceMP3Path);
        File srcFile = new File(sourceMP3Path);
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.d(TAG, "onSuccess: " + convertedFile);
            }

            @Override
            public void onFailure(Exception error) {
                Log.e(TAG, "onFailure: ", error);
            }
        };
        AndroidAudioConverter.with(Execise4Activity.this.getApplicationContext())
                // Your current audio file
                .setFile(srcFile)
                // Your desired audio format
                .setFormat(AudioFormat.AAC)
                // An callback to know when conversion is finished
                .setCallback(callback)
                // Start conversion
                .convert();
    }
}
