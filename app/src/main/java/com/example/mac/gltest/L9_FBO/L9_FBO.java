package com.example.mac.gltest.L9_FBO;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.mac.gltest.R;
import com.example.mac.gltest.util.DebugUtil;
import com.example.mac.gltest.util.ShaderHelper;
import com.example.mac.gltest.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteRenderbuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * FBO离屏渲染
 * Created by yijie.ma on 2018/5/16.
 */

public class L9_FBO implements GLSurfaceView.Renderer {


    private Context mContext;

    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "\n" +
            "attribute vec2 a_texCoord;\n" +
            "varying vec2 v_texCoord;\n" +
            "attribute vec4 a_Position;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    v_texCoord = a_texCoord;\n" +
            "    gl_Position = u_Matrix * a_Position;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "\n" +
            "varying vec2 v_texCoord;\n" +
            "uniform sampler2D u_TextureUnit;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\n" +
            "gl_FragColor = texture2D(u_TextureUnit, v_texCoord);\n" +
            "\n" +
            "}";


    private float cubePositions[] = {
            -1f, 1f,    //正面左上0
            -1.0f, -1.0f, //正面左下1
            1.0f, -1.0f,  //正面右下2
            1.0f, 1.0f   //正面右上3
    };

    private final short index[] = {
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2     //下面
    };

    //八个顶点的颜色，与顶点坐标一一对应
    private float color[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f
    };


    private final float[] mProjectionMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };
    /**
     * 纹理坐标
     */
    private static final float[] TEX_VERTEX = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };

    //片段着色器中定义的一个uniform颜色变量
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private static final String A_TEXCOORD = "a_texCoord";
    private static final String U_TEXTURE = "u_TextureUnit";

    private int aPositionLocation;
    private int uMatrixLocation;
    private int uTextureLocation;
    private int aTexCoordLocation;


    private int program;
    private int mTextureId;

    private FloatBuffer vertexData;
    private FloatBuffer mTexCoordData;
    private FloatBuffer mColorData;
    private int[] mFrameBuffer = new int[1];
    private int[] mRenderBuffer = new int[1];
    private int[] mFrameBufferTextureIds = new int[1];


    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 4;
    private static final int TEXTURE_POSITION_COMPONENT_COUNT = 2;
    //存储生成的矩阵的数组
    private final float[] projectMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    private int mWidth, mHeight;

    public L9_FBO(Context context) {
        mContext = context;


        vertexData = ByteBuffer
                .allocateDirect(cubePositions.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(cubePositions);


        mColorData = ByteBuffer
                .allocateDirect(color
                        .length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mColorData.put(color);


        mTexCoordData = ByteBuffer.allocateDirect(TEX_VERTEX
                .length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordData.put(TEX_VERTEX);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //开启深度测试
        glClearColor(1f, 1.0f, 1.0f, 1.0f);


        int vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER);
        int fragmentShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        glUseProgram(program);
        // 开启纹理透明混合，这样才能绘制透明图片
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);


        uTextureLocation = glGetUniformLocation(program, U_TEXTURE);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTexCoordLocation = glGetAttribLocation(program, A_TEXCOORD);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);


        mTextureId = TextureHelper.loadTexture(mContext, R.drawable.blackman);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        mWidth = width;
        mHeight = height;
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        // 由于Android屏幕上绘制的起始点在左上角，而GL坐标是在左下角，所以需要进行水平翻转，即Y轴翻转
        Matrix.scaleM(mProjectionMatrix, 0, 1, -1, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        //绑定渲染缓冲对象
        glGenRenderbuffers(1, mRenderBuffer, 0);
        // 将RenderBuffer挂载到FrameBuffer上，存储深度信息
        glBindRenderbuffer(GL_RENDERBUFFER, mRenderBuffer[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, mWidth, mHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_RENDERBUFFER, mRenderBuffer[0]);


        //绑定帧缓冲对象
        glGenFramebuffers(1, mFrameBuffer, 0);
        //给帧缓存创建一个纹理
        glGenTextures(1, mFrameBufferTextureIds, 0);
        glBindTexture(GL_TEXTURE_2D, mFrameBufferTextureIds[0]);
        glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                mWidth, mHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffer[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mFrameBufferTextureIds[0], 0);
        // 将纹理对象挂载到FrameBuffer上，存储颜色信息
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, mRenderBuffer[0]);


        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0);
        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        glUniform1i(uTextureLocation, 0);


        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);


        mTexCoordData.position(0);
        glVertexAttribPointer(aTexCoordLocation, TEXTURE_POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, mTexCoordData);
        glEnableVertexAttribArray(aTexCoordLocation);


        glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

        Log.w("MYJ ", " mFrameBufferTextureIds ---> " + mFrameBufferTextureIds[0]);

        glDeleteFramebuffers(1, mFrameBuffer, 0);
        glDeleteRenderbuffers(1, mRenderBuffer, 0);
        GLES20.glDeleteTextures(1, mFrameBufferTextureIds, 0);
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_FRAMEBUFFER, 0);
//        GLES20.glDeleteProgram(program);


        DebugUtil.readPixels(mWidth, mHeight);
    }



}
