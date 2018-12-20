package it.zerozero.belclock;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by David on 06/11/2016.
 */

public class Oscillator implements Runnable {

    private boolean a;
    private Handler hnd;
    private long halfCycle;
    public static boolean goFlag;
    public static int OFF = 10000;
    public static int ON = 10001;

    public Oscillator(Handler hnd, long halfCycle) {
        this.hnd = hnd;
        this.halfCycle = halfCycle;
    }

    @Override
    public void run() {
        goFlag = true;
        a = false;
        Log.i("Oscillator", "run()");
        while (goFlag){
            Message msg = new Message();
            if(a){
                msg.what = ON;
                a = false;
            }
            else if(!a){
                msg.what = OFF;
                a = true;
            }
            hnd.sendMessage(msg);
            try {
                Thread.sleep(halfCycle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
