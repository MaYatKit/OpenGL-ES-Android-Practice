package com.example.mac.gltest.L6_Texture;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.mac.gltest.R;
import com.example.mac.gltest.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * 第六章，用纹理增加细节
 * <br>主要是学习使用图片来给着色器着色 <br>
 * Created by yijie.ma on 2017/11/10.
 */

public class L6_Render implements GLSurfaceView.Renderer {


    private final Context mContext;


    //存储生成的矩阵的数组
    private final float[] projectMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table mTable;
    private Mallet mMallet;

    private ShaderProgram.TextureShaderProgram mTextureShaderProgram;
    private ShaderProgram.ColorShaderProgram mColorShaderProgram;

    private int texture;
    private int texture2;


    public L6_Render(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        glClearColor(0f,0f,0f,0f);

        mTable = new Table();
        mMallet = new Mallet();

        mTextureShaderProgram = new ShaderProgram.TextureShaderProgram(mContext);
        mColorShaderProgram = new ShaderProgram.ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
        texture2 =  TextureHelper.loadTexture(mContext, R.drawable.blackman);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        glViewport(0, 0, w, h);

        //计算正交投影，分别是竖屏和横屏
        float aspectRatio = w > h ? w / (float) h : h / (float) w;
        if (w > h) {
            Matrix.orthoM(projectMatrix, 0, -aspectRatio / 2f, aspectRatio / 2f, -0.5f, 0.5f, -1, 1);
        } else {
            Matrix.orthoM(projectMatrix, 0, -0.5f, 0.5f, -aspectRatio / 2f, aspectRatio / 2f, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

//        mTextureShaderProgram.useProgram();
//        mTextureShaderProgram.setUniforms(projectMatrix, texture, texture2);
//        mTable.bindData(mTextureShaderProgram);
//        mTable.draw();
//
//
//
//        mColorShaderProgram.useProgram();
//        mColorShaderProgram.setUniforms(projectMatrix);
//        mMallet.bindData(mColorShaderProgram);
//        mMallet.draw();


    }
}
