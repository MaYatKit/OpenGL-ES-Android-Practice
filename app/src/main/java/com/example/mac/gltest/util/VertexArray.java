package com.example.mac.gltest.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * 用于储存和加载顶点着色器的顶点坐标
 * Created by yijie.ma on 2018/5/5.
 */

public class VertexArray {
    //一个浮点类型占四个字节
    public static final int BYTES_PER_FLOAT = 4;

    private final FloatBuffer mFloatBuffer;

    public VertexArray(float[] vertexData) {
        mFloatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mFloatBuffer.put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        mFloatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, mFloatBuffer);
        glEnableVertexAttribArray(attributeLocation);

        mFloatBuffer.position(0);

    }

    public void updateBuffer(float[] vertexData, int start, int count){
        mFloatBuffer.position(start);
        mFloatBuffer.put(vertexData, start, count);
        mFloatBuffer.position(0);
    }


}
