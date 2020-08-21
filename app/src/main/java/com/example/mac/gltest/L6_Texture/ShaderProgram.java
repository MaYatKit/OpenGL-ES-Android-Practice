package com.example.mac.gltest.L6_Texture;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import com.example.mac.gltest.util.ShaderHelper;

import static android.opengl.GLES20.*;

/**
 * Created by yijie.ma on 2018/5/6.
 */

public class ShaderProgram {

    private static final String TAG = "ShaderProgram";

    protected static final String U_MATRIX = "u_Matrix";

    protected static final String A_POSITION = "a_Position";

    protected final int program;


    public ShaderProgram(Context context, String verShader, String fragShader) {
        program = ShaderHelper.buildProgram(verShader, fragShader);
        Log.w(TAG, " create a program successfully: " + program);
    }

    public void useProgram() {
        //设置当前要操作的GL程序
        GLES20.glUseProgram(program);
    }


    public static class TextureShaderProgram extends ShaderProgram {

        //纹理顶点着色器
        private static final String TEXTURE_VERTEX_SHADER = "" +
                "uniform mat4 u_Matrix;\n" +
                "\n" +
                "attribute vec4 a_Position;\n" +
                "attribute vec2 a_TextureCoordinates;\n" +
                "attribute vec2 a_TextureCoordinates2;\n" +
                "\n" +
                "varying vec2 v_TextureCoordinates;\n" +
                "\n" +
                "varying vec2 v_TextureCoordinates2;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    v_TextureCoordinates = a_TextureCoordinates;\n" +
                "    v_TextureCoordinates2 = a_TextureCoordinates2;\n" +
                "\n" +
                "    gl_Position = a_Position * u_Matrix;\n" +
                "}";

        //纹理片段着色器
        private static final String TEXTURE_FRAGMENT_SHADER = "" +
                "precision mediump float;\n" +
                "\n" +
                "uniform sampler2D u_TextureUnit;\n" +
                "\n" +
                "varying vec2 v_TextureCoordinates;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "\n" +
                "gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);\n" +
                "\n" +
                "}";

        private static final String U_TEXTURE_UNIT = "u_TextureUnit";

        private static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

        private static final String U_TEXTURE_UNIT2 = "u_TextureUnit2";

        private static final String A_TEXTURE_COORDINATES2 = "a_TextureCoordinates2";


        private final int uMatrixLocation;
        private final int uTextureUnitLocation;
        private final int uTextureUnitLocation2;

        private final int aPositionLocation;
        private final int aTextureCoordinatesLocation;
        private final int aTextureCoordinatesLocation2;


        public TextureShaderProgram(Context context) {
            super(context, TEXTURE_VERTEX_SHADER, TEXTURE_FRAGMENT_SHADER);

            //获取两个uniform变量，一个是矩阵，一个是2D纹理
            uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
            uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
            uTextureUnitLocation2 = glGetUniformLocation(program, U_TEXTURE_UNIT2);

            //获取两个attribute变量，一个是顶点坐标，一个是纹理坐标
            aPositionLocation = glGetAttribLocation(program, A_POSITION);
            aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
            aTextureCoordinatesLocation2 = glGetAttribLocation(program, A_TEXTURE_COORDINATES2);
        }

        public void setUniforms(float[] matrix, int textureId) {
            //给变量u_Matrix赋值
            glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

            //设置当前操作的纹理为GL_TEXTURE0
            glActiveTexture(GL_TEXTURE0);

            //将textureId与当前活动的纹理即GL_TEXTURE0绑定
            glBindTexture(GL_TEXTURE_2D, textureId);

            //将GL_TEXTURE0与着色器中的u_TextureUnit绑定
            glUniform1i(uTextureUnitLocation, 0);

        }

        public int getPositionAttributeLocation() {
            return aPositionLocation;
        }

        public int getaTextureCoordinatesLocation() {
            return aTextureCoordinatesLocation;
        }


        public int getaTextureCoordinatesLocation2() {
            return aTextureCoordinatesLocation2;
        }
    }


    public static class ColorShaderProgram extends ShaderProgram {
        private static final String VERTEX_SHADER = "" +
                "uniform mat4 u_Matrix;\n" +
                "\n" +
                "attribute vec4 a_Position;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = a_Position * u_Matrix;\n" +
                "}";

        private static final String FRAGMENT_SHADER = "" +
                "precision mediump float;\n" +
                "\n" +
                "uniform vec4 u_Color;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "\n" +
                "gl_FragColor = u_Color;\n" +
                "\n" +
                "}";


        private static final String A_COLOR = "a_Color";
        protected static final String U_COLOR = "u_Color";


        //Uniform变量，指向一个Matrix
        private final int uMatrixLocation;

        //两个Attribute变量，分别指向坐标和颜色
        private final int aPositionLocation;
        private final int uColorLocation;


        public ColorShaderProgram(Context context) {
            super(context, VERTEX_SHADER, FRAGMENT_SHADER);

            //获取这个着色器里面的uniform变量，即U_MATRIX
            uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

            //获取这个着色器里面的两个attribute变量，即A_POSITION和A_COLOR
            aPositionLocation = glGetAttribLocation(program, A_POSITION);
            uColorLocation = glGetUniformLocation(program, U_COLOR);
        }


        public void setUniforms(float[] matrix, float r, float g , float b) {
            //传递matrix进去这个着色器里
            glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

            glUniform4f(uColorLocation, r, g, b, 1f);
        }


        public int getPositionAttributeLocation() {
            return aPositionLocation;
        }
    }


}
