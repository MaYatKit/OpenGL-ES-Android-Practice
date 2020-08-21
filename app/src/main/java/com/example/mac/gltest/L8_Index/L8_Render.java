package com.example.mac.gltest.L8_Index;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.mac.gltest.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * 这节课主要是用索引法画图
 * <p>
 * Created by yijie.ma on 2018/5/14.
 */

public class L8_Render implements GLSurfaceView.Renderer {


    private Context mContext;

    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "\n" +
            "attribute vec4 a_Position;\n" +
            "varying  vec4 vColor;\n" +
            "attribute vec4 a_Color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = u_Matrix * a_Position;\n" +
            "    vColor = a_Color;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "\n" +
            "varying  vec4 vColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\n" +
            "gl_FragColor = vColor;\n" +
            "\n" +
            "}";


    private float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f     //反面右上7
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


    //片段着色器中定义的一个uniform颜色变量
    private static final String A_Color = "a_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";

    private int aPositionLocation;
    private int aColorLocation;
    private int uMatrixLocation;
    private int program;
    private FloatBuffer vertexData;
    private FloatBuffer colorData;
    private ShortBuffer shortData;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;
    //存储生成的矩阵的数组
    private final float[] projectMatrix = new float[16];
    private float[] mViewMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    public L8_Render(Context context) {
        mContext = context;


        vertexData = ByteBuffer
                .allocateDirect(cubePositions.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(cubePositions);


        colorData = ByteBuffer
                .allocateDirect(color
                        .length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorData.put(color);


        shortData = ByteBuffer.allocateDirect(index
                .length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        shortData.put(index);
        shortData.position(0);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        glClearColor(1f, 1.0f, 1.0f, 1.0f);


        int vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER);
        int fragmentShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        glUseProgram(program);


        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_Color);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);


        colorData.position(0);
        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, 0, colorData);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        glViewport(0, 0, width, height);

        //计算正交投影，分别是竖屏和横屏
        float aspectRatio = width/(float)height;

        //设置透视投影
        Matrix.frustumM(projectMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,projectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);

//        glUniform4f(aColorLocation, 0.34f, 0.13f, 0f, 1.0f);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, shortData);
//        glDrawArrays(GL_TRIANGLE_FAN, 0, cubePositions.length / 3);


    }
}
