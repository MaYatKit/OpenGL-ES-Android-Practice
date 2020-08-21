package com.example.mac.gltest.L10_Particle;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.mac.gltest.util.ShaderHelper;

/**
 * Created by yijie.ma on 2018/5/26.
 */

class ShaderProgram {

    private static final String TAG = "ShaderProgram";

    protected static final String U_MATRIX = "u_Matrix";

    protected static final String A_POSITION = "a_Position";

    protected static final String A_COLOR = "a_Color";

    protected static final String U_TIME = "u_Time";

    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";

    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    protected static final String U_MOVE = "u_Move";
    protected static final String U_MOVE_MATRIX = "u_MoveMatrix";




    protected final int program;


     ShaderProgram(Context context, String verShader, String fragShader) {
        program = ShaderHelper.buildProgram(verShader, fragShader);
        Log.w(TAG, " create a program successfully: " + program);
    }

    public void useProgram() {
        //设置当前要操作的GL程序
        GLES20.glUseProgram(program);
    }
}
