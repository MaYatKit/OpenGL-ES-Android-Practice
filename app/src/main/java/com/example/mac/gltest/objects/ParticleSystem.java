package com.example.mac.gltest.objects;


import android.graphics.Color;
import android.util.Log;

import com.example.mac.gltest.L10_Particle.ParticleShaderProgram;
import com.example.mac.gltest.util.Geometry;
import com.example.mac.gltest.util.VertexArray;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * 粒子系统
 * Created by yijie.ma on 2018/5/26.
 */

public class ParticleSystem {


    private static final String TAG = "ParticleSystem";
    private static final int BYTES_PER_FLOAT = 4;

    //每个坐标有3个float
    private static final int POSITION_COMPONENT_COUNT = 3;
    //每个颜色有3个float
    private static final int COLOR_COMPONENT_COUNT = 3;
    //每个向量有3个float
    private static final int VECTOR_COMPONENT_COUNT = 3;
    //每个开始时间有1个float
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    //一个点所有信息所占的float
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
                    + COLOR_COMPONENT_COUNT
                    + VECTOR_COMPONENT_COUNT
                    + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;


    private final float[] mParticles;

    private final VertexArray mVertexArray;
    private final int mMaxParticleCount;

    private int mCurrentParticleCount;
    private int mNextParticle;

    public ParticleSystem(int maxParticleCount) {
        mParticles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        mVertexArray = new VertexArray(mParticles);
        mMaxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point postiion, int color, Geometry.Vector direction,
                            float particleStartTime, float[] moveXY) {
        final int particleOffect = mNextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffect;
        mNextParticle++;

        if (mCurrentParticleCount < mMaxParticleCount) {
            mCurrentParticleCount++;
        }

        if (mNextParticle == mMaxParticleCount) {
            //重新从第一个粒子开始
            mNextParticle = 0;
        }

        mParticles[currentOffset++] = postiion.x + moveXY[0];
        mParticles[currentOffset++] = postiion.y + moveXY[1];
        mParticles[currentOffset++] = postiion.z;

        mParticles[currentOffset++] = Color.red(color) / 255f;
        mParticles[currentOffset++] = Color.green(color) / 255f;
        mParticles[currentOffset++] = Color.blue(color) / 255f;

        mParticles[currentOffset++] = direction.x;
        mParticles[currentOffset++] = direction.y;
        mParticles[currentOffset++] = direction.z;

        mParticles[currentOffset++] = particleStartTime;

        mVertexArray.updateBuffer(mParticles, particleOffect, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleProgram) {
        int dataOffset = 0;
        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);

        dataOffset += POSITION_COMPONENT_COUNT;
        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;


        mVertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getParticleStartTimeAttributeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }


    public void draw() {
        glDrawArrays(GL_POINTS, 0, mCurrentParticleCount);
    }

}
