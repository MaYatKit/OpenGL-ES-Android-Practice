package com.example.mac.gltest.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by yijie.ma on 2017/11/13.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        //获取一个着色器对象ID，相当于句柄，可以是片段着色器，也可以是顶点着色器
        final int shaderObjectId = glCreateShader(type);
        //返回0代表创建着色器对象失败，调用glGetError()查询原因
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, " Could not create new shader: "+ glGetError());
            }
            return 0;
        }

        //将着色器glsl代码跟这个着色器对象绑定起来
        glShaderSource(shaderObjectId, shaderCode);
        //编译这个着色器对象
        glCompileShader(shaderObjectId);

        final int[] complieStatus = new int[1];
        //查询这个着色器是否编译成功，并把结果返回到complieStatus中
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, complieStatus, 0);


        if (LoggerConfig.ON) {
            //查询这个着色器详细编译信息
            Log.w(TAG, " Results of comiling source: " + "\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
        }

        //如果等于0，表明着色器编译失败
        if (complieStatus[0] == 0) {
            //删除这个着色器对象
            glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }
        }

        return shaderObjectId;
    }

    /**
     * 将顶点着色器和片段着色器链接在一起
     * @param vertexShaderId 顶点着色器句柄
     * @param fragmentShaderId 片段着色器句柄
     * @return GL程序句柄
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //创建一个GL程序并返回句柄
        final int programObjectId = glCreateProgram();

        //返回0表示创建失败，调用glGetError()查询原因
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, " Could not create new program: " + glGetError());
            }
            return 0;
        }

        //将顶点着色器和这个GL程序绑定起来
        glAttachShader(programObjectId, vertexShaderId);
        //将片段着色器和这个GL程序绑定起来
        glAttachShader(programObjectId, fragmentShaderId);


        //将上面绑定的着色器联合起来
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];

        //检查成功还是失败
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        //打印这个GL程序对象的编译详情
        if (LoggerConfig.ON) {
            Log.w(TAG, " Results of linking program: " + "\n" + glGetProgramInfoLog(programObjectId));
        }

        if (linkStatus[0] == 0) {
            //如果失败，则删除这个GL程序对象
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
        }
        return programObjectId;
    }


    public static boolean validateProgram(int programObjectId) {
        //验证这个GL程序
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];

        //检查并打印结果
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

        Log.w(TAG, " Results of validating program: " + validateStatus[0] + "\n log:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;

        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        //链接顶点着色器和片段着色器程序
        program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON){
            validateProgram(program);
        }

        return program;
    }




}
