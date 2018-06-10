package com.zpw.audiovideo.execise5;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * OpenGL 三角形图形类
 */
public class Triangle {
    private FloatBuffer vertexBuffer;

    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    // 所有的浮点值都是中等精度 （precision mediump float;）
    // 也可以选择把这个值设为“低”（precision lowp float;）或者“高”（precision highp float;）
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private static final int COORDS_PER_VERTEX = 3; // 每个点由3个数值定义
    private static float triangleCoords[] = {       // 逆时针顺序
            0.0f, 0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f   // bottom right
    };

    // 设置red, green, blue and alpha 颜色值
    private float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private final int mProgram;

    public Triangle() {
        // 初始化顶点ByteBuffer
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4); // 1个float占4个byte
        bb.order(ByteOrder.nativeOrder());  // 使用硬件指定的字节顺序 一般而言是ByteOrder.LITTLE_ENDIAN

        vertexBuffer = bb.asFloatBuffer();  // 从ByteBuffer中创建FloatBuffer
        vertexBuffer.put(triangleCoords);   // 将预置的坐标值填入FloatBuffer
        vertexBuffer.position(0);           // 设置从第一个坐标开始

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();// 创建空的OpenGL ES Program

        GLES20.glAttachShader(mProgram, vertexShader);  // ES Program加入顶点着色器
        GLES20.glAttachShader(mProgram, fragmentShader);// ES Program加入片段着色器fragmentShader
        GLES20.glLinkProgram(mProgram);                 // 创建可执行的OpenGL ES程序
    }

    private int mPositionHandle;
    private int mColorHandle;

    private static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private static final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点4字节

    public void draw() {
        GLES20.glUseProgram(mProgram); // 将程序添入OpenGL ES环境中

        // 获取顶点着色器的vPosition成员位置
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle); // 激活这个三角形顶点的handle

        // 准备这个三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取片段着色器的颜色成员信息
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);   // 设置三角形的颜色
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount); // 绘制三角形
        GLES20.glDisableVertexAttribArray(mPositionHandle); // Disable vertex array
    }
}
