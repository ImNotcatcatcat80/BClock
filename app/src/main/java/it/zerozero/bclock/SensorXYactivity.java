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
    private static SensorXYview sensorXYview;
    private static SensorManager sensorManager;
    private static Sensor sensor;
    private static float sensorScale = 1f;

    private boolean isZeroing = false;
    private AppSynchronizer appSynchronizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorxy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_plus = findViewById(R.id.fab_plus);
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorScale = sensorScale / 2;
                Snackbar.make(view, String.format(Locale.ITALIAN, "Scale = %6f", sensorScale), Snackbar.LENGTH_SHORT)
                        .setAction("ScaleUP", null).show();
            }
        });
        FloatingActionButton fab_minus = findViewById(R.id.fab_minus);
        fab_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorScale = sensorScale * 2;
                Snackbar.make(view, String.format(Locale.ITALIAN, "Scale = %6f", sensorScale), Snackbar.LENGTH_SHORT)
                        .setAction("ScaleDN", null).show();
            }
        });

        appSynchronizer = AppSynchronizer.getInstance();
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = appSynchronizer.getSensor(); //sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorScale = sensor.getMaximumRange();
        sensorManager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        headerTextView = findViewById(R.id.headerTextView);
        headerTextView.setText(sensor.getName());

        sensorXYview = findViewById(R.id.SensorXYview);
        sensorXYview.setCircleEnabled(true);
        sensorXYview.setTraceMode(false);
        sensorXYview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sensorXYview.invalidate();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor_xy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 1) {
            sensorXYview.redrawVector(event.values[0] / sensorScale, event.values[1] / sensorScale);
        } else {
            sensorXYview.redrawVector(event.values[0] / sensorScale, 0);
        }
        long timeMillis = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
