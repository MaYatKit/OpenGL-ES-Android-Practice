package com.example.mac.gltest.objects;

import android.opengl.Matrix;
import com.example.mac.gltest.util.Geometry;

import java.util.Random;

/**
 * 粒子喷泉
 * Created by yijie.ma on 2018/6/2.
 */

public class ParticleShooter {

    private final Geometry.Point mPosition;
    private final Geometry.Vector mDirection;
    private final int mColor;


    private final float mAngleVariance;

    private final float mSpeedVariance;

    private final Random random = new Random();

    private float[] mRotationMatrix = new float[16];
    private float[] mDirectionVector = new float[4];
    private float[] mResultVector = new float[4];


    public ParticleShooter(Geometry.Point position, Geometry.Vector direction, int color,
                           float angleVarianceInDegrees, float speedVariance) {
        mPosition = position;
        mDirection = direction;
        mColor = color;


        mAngleVariance = angleVarianceInDegrees;
        mSpeedVariance = speedVariance;

        mDirectionVector[0] = direction.x;
        mDirectionVector[1] = direction.y;
        mDirectionVector[2] = direction.z;

    }


    public void addParticles(ParticleSystem particleSystem, float currentTime, int count, float[] moveXY) {
        for (int i = 0; i < count; i++) {
            Matrix.setRotateEulerM(mRotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * mAngleVariance,
                    (random.nextFloat() - 0.5f) * mAngleVariance,
                    (random.nextFloat() - 0.5f) * mAngleVariance);

            Matrix.multiplyMV(mResultVector, 0, mRotationMatrix, 0, mDirectionVector, 0);

            float speedAdjustment = 1f + random.nextFloat() * mSpeedVariance;

            Geometry.Vector thisDirection = new Geometry.Vector(mResultVector[0] * speedAdjustment,
                    mResultVector[1] * speedAdjustment,
                    mResultVector[2] * speedAdjustment);

            particleSystem.addParticle(mPosition, mColor, thisDirection, currentTime, moveXY);
        }
    }


}
