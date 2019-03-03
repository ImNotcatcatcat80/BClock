package it.zerozero.bclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class BackgroundClockService extends Service {

    public BackgroundClockService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BackgroundCounterAsynctask backgroundCounter = new BackgroundCounterAsynctask();
        backgroundCounter.setContext(getApplicationContext());
        backgroundCounter.execute();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // TODO: 03/03/2019 Make this static
    public class BackgroundCounterAsynctask extends AsyncTask {

        Context context;

        public void setContext(Context c) {
            context = c;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            int ctr = 0;
            while (ctr <= 120) {
                Log.i("ctr", String.valueOf(ctr));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctr++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.i("BackgroundCounter", "done.");
            stopSelf();
        }
    }

}
