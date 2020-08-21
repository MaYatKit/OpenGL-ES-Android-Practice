package com.example.mac.gltest.util;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * 调试工具类
 * Created by yijie.ma on 2018/5/26.
 */

public class DebugUtil {


    /**
     * 读取当前GL屏幕上的画面
     * @param width
     * @param height
     * @return 返回一张bitmap作为结果
     */
    public static Bitmap readPixels(int width, int height) {
        ByteBuffer buffer = ByteBuffer.allocate(width * height * 4);
        GLES20.glReadPixels(0,
                0,
                width,
                height,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, buffer);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }


}
