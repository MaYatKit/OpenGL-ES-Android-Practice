package com.example.mac.gltest.L1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.example.mac.gltest.util.LoggerConfig;
import com.example.mac.gltest.util.ShaderHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by yijie.ma on 2017/11/10.
 */

public class L1_Render implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER = "attribute vec4 a_Position;\n" +
            "attribute vec4 a_color;\n" +
            "\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\n" +
            "    v_color = a_color;\n" +
            "    gl_Position = a_Position;\n" +
            "    //指定OPEN GL里面一个点的大小\n" +
            "    gl_PointSize = 20.0;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\n" +
            "gl_FragColor = v_color;\n" +
            "\n" +
            "}";



    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private Context mContext;

    private int program;

    //片段着色器中定义的一个uniform颜色变量
    private static final String U_COLOR = "u_Color";
    //顶点着色器中定义的一个顶点位置变量
    private static final String A_POSITION = "a_Position";
    private int uColorLocation;
    private int aPositionLocation;

    //顶点着色器中定义每个顶点的颜色变量
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    //顶点着色器中的a_Color的句柄
    private int aColorLocation;


    public L1_Render(Context context) {
        float[] tableVertices = {
                0f, 0f,
                0f, 14f,
                9F, 14F,
                9F, 0F
        };

        //屏幕左边对应X轴-1， 屏幕右边对应+1，屏幕底部对应Y轴-1，屏幕顶部对应+1
        float[] tableVerticesWithTriangles = {

                //最底的棕色桌面
                -0.6f, -0.6f,
                0.6f, 0.6f,
                -0.6f, 0.6F,

                -0.6f, -0.6f,
                0.6f, -0.6f,
                0.6f, 0.6f,

                //上面的白色桌面
                0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.5F, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

                //中间的线
                -0.5f, 0f, 1.0f, 0f, 0f,
                0.5f, 0f, 1.0f, 0f, 0f,

                //下面代表曲棍球的点
                0f, -0.25f, 0f, 0f, 1.0f,
                //上面代表曲棍球的点
                0f, 0.25f, 1.0f, 0f, 0f,


                0f, 0f


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
        glClearColor(1.0f, 0f, 0.0f, 0.0f);

        //创建顶点着色器，这里的"着色器"意思并不是上色，而是创建点，线，或三角形
        int vertexShader = ShaderHelper.compileVertexShader(VERTEX_SHADER);
        //创建片段着色器，这里的"着色器"基本上可以理解为"顶点着色器"创建的点，线，或三角形上色
        int fragmentShader = ShaderHelper.compileFragmentShader(FRAGMENT_SHADER);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            //验证这个GL程序对象，并打印信息
            ShaderHelper.validateProgram(program);
        }

        //告诉OPEN GL使用这个GL程序来绘制屏幕
        glUseProgram(program);

        //获取GL程序中uniform的位置，并存入uColorLocation变量中
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        //获取GL程序中顶点属性的位置，并存入aPositionLocation变量中
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        //将FloatBuffer的内部指针移到开头
        vertexData.position(0);

        //告诉OPEN GL从缓冲区vertexData读取a_Position对应的数据。
        //第一个参数index是a_Position在GL程序中的位置
        //第二个参数size是说明每个属性数据需要几个分量，我们之前定义一个顶点用两个浮点数来表示XY坐标，所以这里传2
        //第三个参数type是说明数据类型，我们定义的是浮点数，所以这里传GL_FLOAT
        //第三个参数normalized只有数据类型是整型时才用到，一般可以忽略
        //第四个参数stride当一个数据数组存储多一个属性时才用到，一般可以忽略并传0
        //第五个参数ptr是告诉OPEN GL去哪里读取数据
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);

        //告诉OPEN GL启用aPositionLocation这个顶点属性
        glEnableVertexAttribArray(aPositionLocation);


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        glViewport(0, 0, w, h);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //清屏
        glClear(GL_COLOR_BUFFER_BIT);

        //给片段着色器代码中的u_Color赋值
        glUniform4f(uColorLocation, 0.34f, 0.13f, 0f, 1.0f);
        //第一个参数告诉OPEN GL想画三角型，第二个参数表示从第几个顶点数据开始读，第三个参数表示往后读取几个顶点数据，这里是六个，可以画两个三角形
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 6, 6);

        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
        glDrawArrays(GL_LINES, 12, 2);

        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f);
        glDrawArrays(GL_POINTS, 14, 1);

        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f);
        glDrawArrays(GL_POINTS, 15, 1);

//        glUniform4f(uColorLocation, 0f, 1f, 0f, 1f);
//        glDrawArrays(GL_POINTS, 16, 1);

    }
}
