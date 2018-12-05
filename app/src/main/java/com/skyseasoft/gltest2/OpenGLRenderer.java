package com.skyseasoft.gltest2;

        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.opengl.GLES20;
        import android.opengl.GLSurfaceView;
        import android.opengl.Matrix;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.os.SystemClock;
        import android.util.Log;
        import android.widget.Toast;

        import com.skyseasoft.gltest2.component.BlockType;
        import com.skyseasoft.gltest2.component.Player;
        import com.skyseasoft.gltest2.screen.GLLight;
        import com.skyseasoft.gltest2.screen.GLObject;
        import com.skyseasoft.gltest2.screen.GLRenderer;
        import com.skyseasoft.gltest2.screen.GLResource;
        import com.skyseasoft.gltest2.screen.GLTexture;
        import com.skyseasoft.gltest2.screen.GLViewer;
        import com.skyseasoft.gltest2.server.JunoComConnector;
        import com.skyseasoft.gltest2.server.Post;
        import com.skyseasoft.gltest2.server.ServerConnector;
        import com.skyseasoft.gltest2.tools.Collision;
        import com.skyseasoft.gltest2.tools.StringHelper;
        import com.skyseasoft.gltest2.tools.VectorHelper;

        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Random;

        import javax.microedition.khronos.egl.EGLConfig;
        import javax.microedition.khronos.opengles.GL10;

/**
 * Created by junodeveloper on 15. 6. 14..
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {
    GLRenderer renderer;
    GLViewer myView;
    GLLight light;
    SuperChunk spChunk;
    Player myPlayer;

    public float mStep;
    public float mJump;
    public int mCreateBlock;
    public int mRemoveBlock;

    private Context mContext;
    private GLSurfaceView mSurfaceView;
    private GLResource res;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public ServerConnector svConn;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public OpenGLRenderer(Context context, GLSurfaceView view) {
        mContext = context;
        mSurfaceView = view;
    }

    public GLRenderer getRenderer() {
        return renderer;
    }
    public SuperChunk getSuperChunk() {
        return spChunk;
    }
    public Player getMyPlayer() {
        return myPlayer;
    }
    public GLResource getResource() {
        return res;
    }
    public ServerConnector getServerConnector() { return svConn; }

    int count = 0;

    long startTime = -1000000;
    long lastServerConnected = -1000000;
    int frames = 0;

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.84f, 1.0f, 1.0f);
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        frames++;
        if(SystemClock.uptimeMillis() - startTime >= 1000) {
            Log.d("FPSCounter", ".fps: " + frames);
            frames = 0;
            startTime = SystemClock.uptimeMillis();
        }

        if(SystemClock.uptimeMillis() - lastServerConnected >= 50) {
            if(svConn.getCreator() != null) {
                svConn.getCreator().sendPlayerInfo(myPlayer);
                //svConn.getCreator().sendRequestPlayers();
                lastServerConnected = SystemClock.uptimeMillis();
            }
        }

        synchronized(svConn.getPlayers()) {
            List<Player> clone = (List<Player>)svConn.getPlayers().clone();
            Iterator<Player> it = clone.iterator();
            while(it.hasNext()) {
                Player player = it.next();
                if(player == null) continue;
                if(player.getId() == myPlayer.getId()) continue;
                Matrix.setIdentityM(res.cuboid.mModelMatrix, 0);
                Matrix.translateM(res.cuboid.mModelMatrix, 0, player.getX(), player.getY(), player.getZ());
                Matrix.rotateM(res.cuboid.mModelMatrix, 0, player.getAngles()[1], 0.0f, 1.0f, 0.0f);
                Matrix.scaleM(res.cuboid.mModelMatrix, 0, 4.0f, 4.0f, 4.0f);
                renderer.drawObject(res.cuboid);
            }
        }

        if(mCreateBlock>0) {
            int[] blockNum = spChunk.getBlockNum(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ());
            spChunk.createBlock(blockNum[0], blockNum[1] - 1, blockNum[2], 1);
            svConn.getCreator().sendNewBlock(blockNum[0], blockNum[1] - 1, blockNum[2], 1);
            mCreateBlock = 0;
        }
        if(mRemoveBlock>0) {
            int[] blockNum = spChunk.getBlockNum(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ());
            spChunk.removeBlock(blockNum[0], blockNum[1] - 1, blockNum[2]);
            svConn.getCreator().sendRemoveBlock(blockNum[0], blockNum[1] - 1, blockNum[2]);
            mRemoveBlock = 0;
        }

        float[] moveForwardPos = myPlayer.getForwardPos(mStep * 0.5f);
        if(!spChunk.isExists(moveForwardPos[0], myPlayer.getY(), moveForwardPos[2]))
        //if(!spChunk.checkCubeCollision(moveForwardPos[0], myPlayer.getY(), moveForwardPos[2]
            myPlayer.setPosition(moveForwardPos[0], myPlayer.getY(), moveForwardPos[2]);
        if(mJump>0) {
            mJump = 0;
            myPlayer.setVelocityY(2.0f);
            myPlayer.updateGravityEffect();
        }
        //if(spChunk.checkCubeCollision(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ(), 8.0f, 8.0f, 8.0f)) {
        if(spChunk.isExists(myPlayer.getX(), myPlayer.getY() - 4.0f, myPlayer.getZ())) {
            myPlayer.setVelocityY(0);
        } else {
            myPlayer.updateGravityEffect();
        }

        if(spChunk.checkGroundCollision(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ(), 8.0f, 8.0f, 8.0f)) {
        //if(spChunk.checkCubeCollision(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ(), 2.0f, 2.0f, 2.0f)) {
            float[] blockPos = spChunk.getBlockPos(myPlayer.getX(), myPlayer.getY() - 4.0f, myPlayer.getZ());
            myPlayer.setPosition(myPlayer.getX(), blockPos[1] + Chunk.BLOCK_SIZE + 4.0f, myPlayer.getZ());
            myPlayer.setVelocityY(0);
        }

        if(spChunk.checkCeilingCollision(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ(), 8.0f, 8.0f, 8.0f)) {
            float[] blockPos = spChunk.getBlockPos(myPlayer.getX(), myPlayer.getY() + 4.0f, myPlayer.getZ());
            myPlayer.setPosition(myPlayer.getX(), blockPos[1] - 4.0f, myPlayer.getZ());
            myPlayer.setVelocityY(0);
        }

        float[] viewPos = myPlayer.getForwardPos(-10);
        myView.updateView(viewPos[0], viewPos[1], viewPos[2], VectorHelper.getDUVector(myPlayer.getAngles()[0], myPlayer.getAngles()[1], myPlayer.getAngles()[2]));

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, res.atlas.texHandle);
        spChunk.render(myPlayer.getX(), myPlayer.getY(), myPlayer.getZ(), VectorHelper.getDUVector(myPlayer.getAngles()[0], myPlayer.getAngles()[1], myPlayer.getAngles()[2]));

        res.cuboid.setTexture(res.tile);
        Matrix.setIdentityM(res.cuboid.mModelMatrix, 0);
        Matrix.translateM(res.cuboid.mModelMatrix, 0, myPlayer.getX(), myPlayer.getY(), myPlayer.getZ());
        Matrix.rotateM(res.cuboid.mModelMatrix, 0, myPlayer.getAngles()[1], 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(res.cuboid.mModelMatrix, 0, 4.0f, 4.0f, 4.0f);
        renderer.drawObject(res.cuboid);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        renderer.resetProjection(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        renderer = new GLRenderer(mContext);
        renderer.setup();
        prepareViewer();
        prepareLight();
        prepareResource();
        spChunk = new SuperChunk(renderer);
        myPlayer = new Player(renderer, res);
        myPlayer.setPosition(800.0f, 100.0f, 800.0f);
        myPlayer.setId(-1); // Initial ID
        prepareSensor();
        prepareServer();
    }

    public void prepareViewer() {
        myView = new GLViewer();
        myView.initView();
        myView.setEyePosition(800.0f, 100.0f, 800.0f);
        myView.updateView();
        renderer.setView(myView);
    }
    public void prepareLight() {
        light = new GLLight();
        light.setPosition(0.0f, 10.0f, 25.0f);
        renderer.setLight(light);
    }
    public void prepareServer() {
        svConn = new ServerConnector(OpenGLRenderer.this);
        svConn.run();
    }
    public void prepareResource() {
        res = new GLResource(mContext);
    }
    public void prepareSensor() {
        mSensorManager = (SensorManager)mContext.getSystemService(mContext.SENSOR_SERVICE);
        mSensorListener = new SensorEventListener() {
            float[] orientationValuesV = new float[3];
            public void onSensorChanged(SensorEvent event) {
                synchronized(myView) {
                    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                        float a1 = (float) Math.atan2(2 * (event.values[0] * event.values[1] + event.values[2] * event.values[3]), 1 - 2 * (event.values[1] * event.values[1] + event.values[2] * event.values[2]));
                        float a2 = (float) Math.asin(2 * (event.values[0] * event.values[2] - event.values[3] * event.values[1]));
                        float a3 = (float) Math.atan2(2 * (event.values[0] * event.values[3] + event.values[1] * event.values[2]), 1 - 2 * (event.values[2] * event.values[2] + event.values[3] * event.values[3]));
                        myPlayer.setAngles(-Rad2Deg(a3) + 90.0f, Rad2Deg(a1), 0);
                    }
                }
            }
            public void onAccuracyChanged(Sensor sensor, int what) {}
            public float Rad2Deg(float radian) {
                return radian * 180 / (float)Math.PI;
            }
        };
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
            }
        }, 0);
    }

    private class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
             /*Toast에 의해서 화면하단에 위도, 경도가 출력됨 */
                Toast.makeText(mContext,
                        "Lat: " + loc.getLatitude() + ", Lng: " + loc.getLongitude() + ", Acc: " + loc.getAccuracy() + ", Alt: " + loc.getAltitude(),
                        Toast.LENGTH_SHORT ).show();
                Log.d("GPSUPDATE", "Lat: " + loc.getLatitude() + ", Lng: " + loc.getLongitude() + ", Acc: " + loc.getAccuracy() + ", Alt: " + loc.getAltitude());
                /* 화면의 상단에 위치한 TextView에 위도, 경도를 출력함*/
                final Location location = loc;
                mSurfaceView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        //texCenterScreen.addText("GPS 정보 수신 결과", 10, 40, 28, 0xff, 0xff, 0xff, 0xff);
                        //texCenterScreen.addText("위도 " + location.getLatitude() + ", 경도 " + location.getLongitude(), 10, 70, 28, 0xff, 0xff, 0xff, 0xff);
                        //texCenterScreen.addText("정확도 " + location.getAccuracy() + "m", 10, 100, 28, 0xff, 0xff, 0xff, 0xff);
                        //texCenterScreen.addText("Geocoder는 아직 지원되지 않습니다.", 10, 130, 28, 0xff, 0xff, 0xff, 0xff);
                        //texCenterScreen.drawAvailableText();
                    }});
                //tex2.drawText("Lat: " + loc.getLatitude() + ", Lng: " + loc.getLongitude() + ", Acc: " + loc.getAccuracy() + ", Alt: " + loc.getAltitude(), 10, 10);
            }
        }
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}