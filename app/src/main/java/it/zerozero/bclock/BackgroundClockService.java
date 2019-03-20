package it.zerozero.bclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackgroundClockService extends Service implements SensorEventListener {

    private Handler bgSvcHandler;
    private Runnable bgSvcRunnable;
    private SensorManager sensorManager;
    private Sensor accel;
    private SharedPreferences mShPref;
    private static long bgCtr;
    private boolean previousOverThrPositive = true;
    private long previousOverThrMs = 0;
    private int numOverThr = 0;
    private long previousToggleMs = 0;
    private boolean flashOn = false;

    public BackgroundClockService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        bgCtr = 0;
        bgSvcRunnable = new BgSvcRunnable();
        bgSvcHandler = new Handler();
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
        bgSvcHandler.post(bgSvcRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("BgSvcRunnable", "service destroyed.");
        bgSvcHandler.removeCallbacks(bgSvcRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Math.abs(event.values[0]) > 9.1) {
            mShPref = getSharedPreferences("BelClock", MODE_PRIVATE);
            if ((event.values[0] > 0) != previousOverThrPositive && mShPref.getBoolean("ControlFlashlight", false)) {
                boolean isOverThrFast = (System.currentTimeMillis() - previousOverThrMs < 1250);
                previousOverThrMs = System.currentTimeMillis();
                if(isOverThrFast) {
                    numOverThr++;
                    previousOverThrPositive = event.values[0] > 0;
                    Log.i("BgSvcRunnable ", "accel X = " + String.valueOf(event.values[0]));
                }
                else {
                    numOverThr = 0;
                }

                if(numOverThr > 1) {
                    Log.i("BgSvcRunnable ", "********************");
                    if (System.currentTimeMillis() - previousToggleMs >= 1500) {
                        toggleFlashSDK23();
                    }
                    numOverThr = 0;
                    previousToggleMs = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void toggleFlashSDK23() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null; // Usually back camera is at 0 position.
            try {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!flashOn) {
                    cameraManager.setTorchMode(cameraId, true); //Turn ON
                    flashOn = true;
                }
                else {
                    cameraManager.setTorchMode(cameraId, false); //Turn OFF
                    flashOn = false;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public class BgSvcRunnable implements Runnable {

        @Override
        public void run() {
            Log.i("BgSvcRunnable ctr ", String.valueOf(bgCtr * 10));
            bgCtr++;
            bgSvcHandler.postDelayed(this, 10000);
        }
    }

}
