package com.example.mac.gltest.L7_Rectangle_Circle;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.mac.gltest.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
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
 *
 * 这节课主要是画圆
 *
 * Created by yijie.ma on 2018/5/14.
 */

public class L7_Render implements GLSurfaceView.Renderer {


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
            "    gl_PointSize = 10.0;\n" +
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


    private static float triangleCoords[] = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f  // top right
    };
    private float colors[] = {
            1f, 1f, 1f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f
    };

    //片段着色器中定义的一个uniform颜色变量
    private static final String U_COLOR = "u_Color";
    private static final String A_Color = "a_Color";
    private static final String A_POSITION = "a_Position";

    private int aPositionLocation;
    private int aColorLocation;
    private int program;
    private FloatBuffer vertexData;
    private FloatBuffer colorData;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;
    private float positions[];
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    //存储生成的矩阵的数组
    private final float[] projectMatrix = new float[16];

    public L7_Render(Context context) {
        mContext = context;

        positions = createPositions(3);

        vertexData = ByteBuffer
                .allocateDirect(positions.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(positions);



        colorData = ByteBuffer
                .allocateDirect(colors
                        .length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorData.put(colors);
    }


    private float[] createPositions(int n) {
        ArrayList<Float> data = new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);             //设置圆心坐标
        float angDegSpan = 360f / n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            data.add((float) (0.5f * Math.sin(i * Math.PI / 180f)));
            data.add((float) (0.5f * Math.cos(i * Math.PI / 180f)));
            data.add(0.0f);
        }
        float[] f = new float[data.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = data.get(i);
        }
        return f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
        glViewport(0, 0, width, height);

        //计算正交投影，分别是竖屏和横屏
        float aspectRatio = width > height ? width / (float) height : height / (float) width;
        if (width > height) {
            Matrix.orthoM(projectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1, 1);
        } else {
            Matrix.orthoM(projectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);


        glUniformMatrix4fv(uMatrixLocation, 1, false, projectMatrix, 0);

//        glUniform4f(aColorLocation, 0.34f, 0.13f, 0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, positions.length / 3);


    }
}
