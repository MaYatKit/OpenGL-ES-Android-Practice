package com.example.mac.gltest.L6_Texture;

import android.opengl.GLES20;

import com.example.mac.gltest.util.VertexArray;

/**
 * 木槌实现类
 * Created by yijie.ma on 2018/5/5.
 */

public class Mallet {
    //一个顶点位置用几个数，这里是xy，所以是2
    private static final int POSTION_COMPONENT_COUNT = 2;
    //一个颜色值用几个数，这里是RGB，所以是3
    private static final int COLOR_COMPONENT_COUNT = 3;
    //跨距，告诉GL读入坐标前跳过几个字节
    private static final int STRIDE = (POSTION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //X,Y,R,G,B
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    private final VertexArray mVertexArray;

    public Mallet() {
        mVertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ShaderProgram.ColorShaderProgram colorProgram) {

        //分别读入顶点坐标和颜色数据
        mVertexArray.setVertexAttribPointer(0, colorProgram.getPositionAttributeLocation(), POSTION_COMPONENT_COUNT, STRIDE);
//        mVertexArray.setVertexAttribPointer(POSTION_COMPONENT_COUNT,  colorProgram.getColorAttributeLocation(), COLOR_COMPONENT_COUNT, STRIDE);
    }


    public void draw() {
        //画木槌
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2);
    }



}
