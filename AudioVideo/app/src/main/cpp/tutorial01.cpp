//
// Created by zpw on 2018/5/21.
//

#include "tutorial01.h"
#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"testff",__VA_ARGS__)

extern "C"
{
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

void tutorial01::SaveFrame(AVFrame *pFrame, int width, int height, int iFrame) {
    FILE *pFile;
    char szFileName[32];
    int y;

    // Open file
    sprintf(szFileName, "/sdcard/frame%d.ppm", iFrame);
    LOGW("szFileName = %s\n", szFileName);
    pFile = fopen(szFileName, "wb");
    if (pFile == NULL)
        return;

    // Write header
    fprintf(pFile, "P6\n%d %d\n255\n", width, height);

    // Write pixel data
    for (int y = 0; y < height; ++y) {
        fwrite(pFrame->data[0] + y * pFrame->linesize[0], 1, width * 3, pFile);
    }

    // Close file
    fclose(pFile);
}

void tutorial01::MakingScreencaps(const char *path) {
    // Initalizing these to NULL prevents segfaults!
    AVFormatContext *pFormatCtx = NULL;
    int i, videoStream;
    AVCodecContext *pCodecCtx = NULL;
    AVCodec *pCodec = NULL;
    AVFrame *pFrame = NULL;
    AVFrame *pFrameRGB = NULL;
    AVPacket *packet;
    int numBytes;
    uint8_t *buffer = NULL;
    struct SwsContext *sws_ctx = NULL;

    if (path == NULL) {
        LOGW("Please provide a movie file\n");
        return;
    }

    //初始化解封装和解码
    av_register_all();
    avcodec_register_all();

    //打开文件，检查头部并保存文件格式的信息
    if (avformat_open_input(&pFormatCtx, path, 0, 0) != 0) {
        LOGW("No such file or directory: %s\n", path);
        return;
    }
    //获取流信息
    if (avformat_find_stream_info(pFormatCtx, 0) != 0) {
        LOGW("Retrieve stream information failded\n");
        return;
    }

    // 查找第一个视频数据流
    videoStream = -1;
    for (int i = 0; i < pFormatCtx->nb_streams; ++i) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            LOGW("视频数据");
            videoStream = i;
            break;
        }
    }
    if (videoStream == -1) {
        LOGW("videoStream == -1\n");
        return;
    }

    //打开视频解码器
    pCodec = avcodec_find_decoder(pFormatCtx->streams[videoStream]->codecpar->codec_id);

    if (pCodec == NULL) {
        LOGW("Unsupported codec!\n");
        return;
    }

    //解码器初始化
    pCodecCtx = avcodec_alloc_context3(pCodec);
    avcodec_parameters_to_context(pCodecCtx, pFormatCtx->streams[videoStream]->codecpar);

    //打开解码器
    if (avcodec_open2(pCodecCtx, pCodec, NULL) != 0) {
        LOGW("avcodec_open2");
        return;
    }

    //读取帧数据
    packet = av_packet_alloc();
    pFrame = av_frame_alloc();
    //存储帧数据
    pFrameRGB = av_frame_alloc();

    // Determine required buffer size and allocate buffer
    numBytes=av_image_get_buffer_size(AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height,1);
    buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

    // Assign appropriate parts of buffer to image planes in pFrameRGB
    // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
    // of AVPicture
    av_image_fill_arrays(pFrameRGB->data, pFrameRGB->linesize, buffer, AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height, 1);

    //初始化像素格式转换的上下文
    sws_ctx = sws_getContext(pCodecCtx->width,
                             pCodecCtx->height,
                             pCodecCtx->pix_fmt,
                             pCodecCtx->width,
                             pCodecCtx->height,
                             AV_PIX_FMT_RGB24,
                             SWS_BILINEAR,
                             NULL,
                             NULL,
                             NULL
    );

    i = 0;
    for (;;) {
        int re = av_read_frame(pFormatCtx, packet);
        AVCodecContext *cc = pCodecCtx;
        //发送到线程中解码
        re = avcodec_send_packet(cc, packet);
        //清理
        av_packet_unref(packet);
        if (re != 0) {
            LOGW("avcodec_send_packet failed!");
            continue;
        }
        for (;;) {
            //接收线程中返回的解码数据
            re = avcodec_receive_frame(cc, pFrame);
            if (re != 0) {
                break;
            }
            sws_scale(sws_ctx, (uint8_t const * const *)pFrame->data,
                      pFrame->linesize, 0, pCodecCtx->height,
                      pFrameRGB->data, pFrameRGB->linesize);
            if (++i <= 5)
                SaveFrame(pFrameRGB, pCodecCtx->width, pCodecCtx->height, i);
        }
    }

    // Free the RGB image
    av_free(buffer);
    av_frame_free(&pFrameRGB);

    // Free the YUV frame
    av_frame_free(&pFrame);

    // Close the codecs
    avcodec_close(pCodecCtx);

    // Close the video file
    avformat_close_input(&pFormatCtx);
}

tutorial01::tutorial01() {

}