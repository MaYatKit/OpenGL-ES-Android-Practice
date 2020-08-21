package com.example.mac.gltest.L10_Particle;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import com.example.mac.gltest.R;
import com.example.mac.gltest.objects.ParticleShooter;
import com.example.mac.gltest.objects.ParticleSystem;
import com.example.mac.gltest.util.Geometry;
import com.example.mac.gltest.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * 粒子系统
 * Created by yijie.ma on 2018/5/26.
 */

public class L10_Particle_Render implements GLSurfaceView.Renderer {

    private Context mContext;

    //存储生成的矩阵的数组
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mViewProjectionMatrix = new float[16];
    private final float[] mMoveVec = new float[2];

    private final float[] mMoveMatrix = new float[16];
    private final float[] mMatrix = new float[16];
    private final float[] mInvertViewProjectionMatrix = new float[16];

    private ParticleShaderProgram mParticleShaderProgram;
    private ParticleSystem mParticleSystem;
    private ParticleShooter mGreenParticleShooter;
    private long mGlobalStartTime;

    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 0.5f;
    private int mTexture;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private float aspectRatio;

    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "uniform float u_Time;\n" +
            "uniform vec2 u_Move;\n" +
            "uniform mat4 u_MoveMatrix;\n" +
            "\n" +
            "attribute vec3 a_Color;\n" +
            "attribute vec3 a_Position;\n" +
            "attribute vec3 a_DirectionVector;\n" +
            "attribute float a_ParticleStartTime;\n" +
            "\n" +
            "varying vec3 v_Color;\n" +
            "varying float v_ElapsedTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    v_Color = a_Color;\n" +
            "    v_ElapsedTime = u_Time - a_ParticleStartTime;\n" +
            "    float gravityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;\n" +
            "    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);\n" +
//            "    currentPosition.x += u_Move.x;\n" +
//            "    currentPosition.y += u_Move.y;\n" +
            "    currentPosition.y -= gravityFactor;\n" +
            "    gl_Position = u_Matrix * vec4(currentPosition, 1.0);\n" +
            "    gl_PointSize = 25.0;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform sampler2D u_TextureUnit;\n" +
            "\n" +
            "varying vec3 v_Color;\n" +
            "varying float v_ElapsedTime;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "gl_FragColor = vec4(v_Color/v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);\n" +
            "}";


    public L10_Particle_Render(Context context) {
        mContext = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //开启深度测试
        glClearColor(0f, 0f, 0f, 0f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        mParticleShaderProgram = new ParticleShaderProgram(mContext, VERTEX_SHADER, FRAGMENT_SHADER);
        mParticleSystem = new ParticleSystem(1000);
        mGlobalStartTime = System.nanoTime();

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        mGreenParticleShooter = new ParticleShooter(new Geometry.Point(0f, 0f, 0f), particleDirection, Color.rgb(25, 255, 25),
                angleVarianceInDegrees, speedVariance);


        mTexture = TextureHelper.loadTexture(mContext, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        mSurfaceWidth = width;
        mSurfaceHeight = height;

        if (mSurfaceWidth > mSurfaceHeight) {
            aspectRatio = mSurfaceWidth / (float) mSurfaceHeight;
            Matrix.orthoM(mMoveMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -2f, 2f);
        } else {
            aspectRatio = mSurfaceHeight / (float) mSurfaceWidth;
            Matrix.orthoM(mMoveMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -2f, 2f);
        }
//
//        //透视矩阵投影
//        MatrixHelper.perspectiveM(mProjectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//        Matrix.setIdentityM(mViewMatrix, 0);
//        Matrix.translateM(mViewMatrix, 0, 0f, 0f, -5f);
//        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


//        MatrixHelper.perspectiveM(mProjectionMatrix, 45f, width / (float) height, 1f, 10f);
//        Matrix.setLookAtM(mViewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//        Matrix.invertM(mInvertViewProjectionMatrix, 0, mViewProjectionMatrix ,0);


//        Matrix.multiplyMM(mMatrix, 0, mViewProjectionMatrix, 0, mMoveMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        //当前粒子的时间
        float currentTime = (System.nanoTime() - mGlobalStartTime) / 1000000000f;

        mGreenParticleShooter.addParticles(mParticleSystem, currentTime, 5, mMoveVec);

        mParticleShaderProgram.useProgram();
        //通知顶点着色器过了多少时间，以改变粒子的位置
        mParticleShaderProgram.setUniforms(mMoveMatrix, currentTime, mTexture);

//        mParticleShaderProgram.setMove(mMoveVec);
//        mParticleShaderProgram.setMoveMatrix(mMoveMatrix);

        //把刷新着色器中的各个变量
        mParticleSystem.bindData(mParticleShaderProgram);
        //画粒子

        mParticleSystem.draw();
    }



    public void handleTouchPress(float nX, float nY) {
        Log.d("handleTouchPress", "nX = " + nX + "nY = " + nY);
        if (mSurfaceWidth > mSurfaceHeight) {
            nX *= aspectRatio;
        } else {
            nY *= aspectRatio;
        }
        mMoveVec[0] = nX;
        mMoveVec[1] = nY;

    }


    public void handleTouchDrag(float nX, float nY) {
        Log.d("handleTouchDrag", "nX = " + nX + "nY = " + nY);
        if (mSurfaceWidth > mSurfaceHeight) {
            nX *= aspectRatio;
        } else {
            nY *= aspectRatio;
        }
        mMoveVec[0] = nX;
        mMoveVec[1] = nY;

    }


}
