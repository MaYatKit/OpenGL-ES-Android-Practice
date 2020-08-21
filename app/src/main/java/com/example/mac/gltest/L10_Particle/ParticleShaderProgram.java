package com.example.mac.gltest.L10_Particle;

import android.content.Context;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE3;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * 粒子着色器程序
 * Created by yijie.ma on 2018/5/26.
 */

public class ParticleShaderProgram extends ShaderProgram {

    //Uniform 变量
    private int uMatrixLocation;
    private int uTimeLocation;


    //Attribute 变量
    private int aPostionLocation;
    private int aColorLocation;
    private int aDirectionVectorLocation;
    private int aParticleStartTimeLocation;

    private final int uTextureUnitLocation;
    private final int uMoveLocation;
    private final int uMoveMatrixLocation;


    ParticleShaderProgram(Context context, String verShader, String fragShader) {
        super(context, verShader, fragShader);

        //从着色器程序中获取Uniform 变量的句柄
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uMoveLocation = glGetUniformLocation(program, U_MOVE);
        uMoveMatrixLocation = glGetUniformLocation(program, U_MOVE_MATRIX);

        //从着色器程序中获取Attribute变量的句柄
        aPostionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);

    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);

        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 3);
    }

    public void setMove(float[] move){
        glUniform2fv(uMoveLocation, 1,move, 0);
    }

    public void setMoveMatrix(float[] moveMatrix){
        glUniformMatrix4fv(uMoveMatrixLocation, 1, false, moveMatrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPostionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }

    public int getDirectionVectorAttributeLocation() {
        return aDirectionVectorLocation;
    }

    public int getParticleStartTimeAttributeLocation() {
        return aParticleStartTimeLocation;
    }




}
