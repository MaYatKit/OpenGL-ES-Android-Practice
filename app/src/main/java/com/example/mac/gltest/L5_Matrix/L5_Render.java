package com.example.mac.gltest.L5_Matrix;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.mac.gltest.util.LoggerConfig;
import com.example.mac.gltest.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * 第五章，调整屏幕的宽高比
 * <br>主要是学习使用正交投影来将归一化坐标转化为虚拟坐标 <br>
 * Created by yijie.ma on 2017/11/10.
 */

public class L5_Render implements GLSurfaceView.Renderer {


    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "\n" +
            "attribute vec4 a_Position;\n" +
            "attribute vec4 a_Color;\n" +
            "\n" +
            "varying vec4 v_Color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    v_Color = a_Color;\n" +
            "\n" +
            "    gl_Position = a_Position * u_Matrix;\n" +
            "    gl_PointSize = 10.0;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "\n" +
            "varying vec4 v_Color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\n" +
            "gl_FragColor = v_Color;\n" +
            "\n" +
            "}";

    //顶点着色器中定义的矩阵的名字
    private static final String U_MATRIX = "u_Matrix";
    //存储生成的矩阵的数组
    private final float[] projectMatrix = new float[16];
    //存储GL程序中这个矩阵的句柄
    private int uMatrixLocation;

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context mContext;

    private int program;

    private static final String A_POSITION = "a_Position";
    private int aColorLocation;
    private int aPositionLocation;

    private static final String A_Color = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    public L5_Render(Context context) {
        float[] tableVertices = {
                0f, 0f,
                0f, 14f,
                9F, 14F,
                9F, 0F
        };
        float[] tableVerticesWithTriangles = {
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8F, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 0f, 1f, 0f,

                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
        };

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

        mContext = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 1f, 1.0f, 1.0f);


        int vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER);
        int fragmentShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program, A_Color);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);


        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        //从第一个颜色属性开始读取，跳过前两个字节
        vertexData.position(POSITION_COMPONENT_COUNT);
        //详细请看书本54页
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //开启a_Color对应的属性数组
        glEnableVertexAttribArray(aColorLocation);

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

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectMatrix, 0);


//        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

//        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

//        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f);
        glDrawArrays(GL_POINTS, 8, 1);

//        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f);
        glDrawArrays(GL_POINTS, 9, 1);


    }
}
