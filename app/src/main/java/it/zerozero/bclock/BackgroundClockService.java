package it.zerozero.bclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackgroundClockService extends Service {

    private Handler bgSvcHandler;
    private Runnable bgSvcRunnable;
    private static long bgCtr;

    public BackgroundClockService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        bgCtr = 0;
        bgSvcRunnable = new BgSvcRunnable();
        bgSvcHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bgSvcHandler.post(bgSvcRunnable);
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
        bgSvcHandler.removeCallbacks(bgSvcRunnable);
    }

    public class BgSvcRunnable implements Runnable {

        @Override
        public void run() {
            if (bgCtr <= 120) {
                Log.i("BgSvcRunnable ctr", String.valueOf(bgCtr));
                bgCtr++;
            }
            else {
                Log.i("BgSvcRunnable ctr", "done.");
                stopSelf();
            }
            bgSvcHandler.postDelayed(this, 1000);
        }
    }

}
