package com.skyseasoft.gltest2;

import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private GLSurfaceView glSurfaceView;
    private OpenGLRenderer glRenderer;

    private float previousX;
    private float previousY;

    private DisplayMetrics displayMetrics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        /*float deltaX = (x - previousX) / displayMetrics.density / 2.0f;
                        float deltaY = (y - previousY) / displayMetrics.density / 2.0f;
                        Log.d("SCREENSIZE", displayMetrics.widthPixels + "," + displayMetrics.heightPixels);
                        glRenderer.mDeltaX = deltaX;
                        glRenderer.mDeltaY = deltaY;*/
                        if(y<displayMetrics.heightPixels/3)
                            glRenderer.mStep = 1.0f;
                        else if(y<2*displayMetrics.heightPixels/3) {
                            if(x<displayMetrics.widthPixels/5)
                                glRenderer.mCreateBlock = 1;
                            else if(x<4 * displayMetrics.widthPixels/5)
                                glRenderer.mJump = 1.0f;
                            else
                                glRenderer.mRemoveBlock = 1;
                        } else
                            glRenderer.mStep = -1.0f;
                        break;
                    case MotionEvent.ACTION_UP:
                        glRenderer.mStep = 0;
                        break;
                }
                previousX = x;
                previousY = y;
                return true;
            }
        });

        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if(supportsEs2) {
            glRenderer = new OpenGLRenderer(this, glSurfaceView);
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(glRenderer);
        } else {
            Toast.makeText(this, "이 폰은 OpenGL ES 2.0을 지원하지 않습니다.", Toast.LENGTH_SHORT);
        }

        setContentView(glSurfaceView);
    }



    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        if(glRenderer.getServerConnector().getPacketManager() != null)
            glRenderer.getServerConnector().getPacketManager().disconnect();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Open The GL", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
