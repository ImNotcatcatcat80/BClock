package it.zerozero.bclock;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class SensorXYactivity extends AppCompatActivity implements SensorEventListener {

    private static TextView headerTextView;
    private static SensorXYview drawing2Dview;
    private static SensorManager sensorManager;
    private static Sensor accelSensor;
    private long previousTimeMillis = System.currentTimeMillis();
    private float motionX, motionY;
    private float zeroX = 0f;
    private float zeroY = 0f;
    private boolean isZeroing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorxy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_play = findViewById(R.id.fab_plus);
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scale UP", Snackbar.LENGTH_LONG)
                        .setAction("ScaleUP", null).show();
                motionX = motionY = 0f;
                isZeroing = true;
            }
        });
        FloatingActionButton fab_pause = findViewById(R.id.fab_minus);
        fab_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Scale DOWN", Snackbar.LENGTH_LONG)
                        .setAction("ScaleDN", null).show();
                motionX = motionY = 0f;
                isZeroing = true;
            }
        });

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);

        headerTextView = findViewById(R.id.headerTextView);
        headerTextView.setText("X Y Axis view");

        drawing2Dview = findViewById(R.id.SensorXYview);
        drawing2Dview.setCircleEnabled(true);
        drawing2Dview.setTraceMode(false);
        drawing2Dview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // drawing2Dview.processMotionEvent(event);
                return true;
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        drawing2Dview.redrawVectorMotion(event.values[0], event.values[1], motionX, motionY);
        long timeMillis = System.currentTimeMillis();

        if(isZeroing) {
            zeroX = event.values[0];
            zeroY = event.values[1];
            isZeroing = false;
        }
        motionX = 0;
        motionY = 0;
        headerTextView.setText(String.format(Locale.ITALIAN, "accelermeter X Y"));
        previousTimeMillis = timeMillis;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
