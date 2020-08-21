package com.example.mac.gltest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.mac.gltest.L10_Particle.L10_Particle_Render;
import com.example.mac.gltest.L11_Touch.L11_Touch;

public class MainActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    private boolean rendererSet = false;

    private L10_Particle_Render mRender;

    private L11_Touch m11Render;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);

        mGLSurfaceView.setEGLContextClientVersion(2);

        mRender = new L10_Particle_Render(this);

//        m11Render = new L11_Touch(this);

        mGLSurfaceView.setRenderer(mRender);
//        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        rendererSet = true;

        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                mRender.handleTouchPress(normalizedX, normalizedY);
                            }
                        });
                    }else if (event.getAction() == MotionEvent.ACTION_MOVE){
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                mRender.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });
                    }
                    return true;
                }else {
                    return false;
                }
            }
        });


        setContentView(mGLSurfaceView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            mGLSurfaceView.onResume();
        }

//        for (int i = 0; i < 20; i++) {
//            mGLSurfaceView.requestRender();
//        }
    }


}
