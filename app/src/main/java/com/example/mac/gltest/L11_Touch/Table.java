package com.example.mac.gltest.L11_Touch;

import android.opengl.GLES20;
import com.example.mac.gltest.util.VertexArray;

/**
 * Created by yijie.ma on 2018/5/5.
 */

public class Table {
    //一个顶点位置用几个数，这里是xy，所以是2
    private static final int POSTION_COMPONENT_COUNT = 2;
    //一个纹理位置用几个数，这里是st，所以是2
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    //跨距，告诉GL读入坐标前跳过几个字节
    private static final int STRIDE = (POSTION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //储存顺序：x,y,s,t
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0.1875f, 1f,
            0.5f, -0.8f, 0.8125f, 1f,
            0.5f, 0.8f, 0.8125f, 0f,
            -0.5f, 0.8f, 0.1875f, 0f,
            -0.5f, -0.8f, 0.1875f, 1f
    };


    private final VertexArray mVertexArray;

    public Table() {
        mVertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ShaderProgram.TextureShaderProgram textureShaderProgram) {

        //分别读入顶点坐标和纹理坐标数据
        mVertexArray.setVertexAttribPointer(0, textureShaderProgram.getPositionAttributeLocation(), POSTION_COMPONENT_COUNT, STRIDE);
        mVertexArray.setVertexAttribPointer(POSTION_COMPONENT_COUNT, textureShaderProgram.getaTextureCoordinatesLocation(), TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);

    }

    public void draw() {
        //画桌子
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }


}
