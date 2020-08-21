package com.example.mac.gltest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenerateMipmap;

/**
 * Created by yijie.ma on 2018/5/5.
 */

public class TextureHelper {

    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resId) {
        //用来存储纹理ID的数组
        final int[] textureObjectIds = new int[1];
        //向GL申请一个纹理ID对象，并写入对象的句柄到textureObjectIds中
        GLES20.glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.e(TAG, "Could not generate a new openGL texture object!");
            }
            return 0;
        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.e(TAG, "Could not decode resourceId: " + resId + " !");
            }
            //如果加载resId的图片失败就删除刚刚生成的纹理对象
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        //告诉GL之后进行的纹理操作是应用在哪个纹理对象上
        GLES20.glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        //设置当前纹理放大缩小采用的过滤模式
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //加载bitmap到当前纹理
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //通知GL生成这个纹理的mipmap贴图
        glGenerateMipmap(GL_TEXTURE_2D);

        //回收bitmap
        bitmap.recycle();
        //通知GL之后的操作不再作用于当前纹理
        glBindTexture(GL_TEXTURE_2D, 0);

        //返回这个纹理对象Id，即是句柄
        return textureObjectIds[0];
    }


}
