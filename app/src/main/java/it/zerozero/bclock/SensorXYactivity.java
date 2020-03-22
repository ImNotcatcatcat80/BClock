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
import android.widget.Toast;

import java.util.Locale;

public class SensorXYactivity extends AppCompatActivity implements SensorEventListener {

    private static TextView headerTextView;
    private static SensorXYview sensorXYview;
    private static SensorManager sensorManager;
    private static Sensor sensor;
    private static MenuItem filteringMenuItem;
    private static float sensorScale = 1f;
    private int num_samples = 1;
    private int sampling_cycle = 0;
    private float[] samples = new float[2];
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sensor_xy, menu);
        filteringMenuItem = menu.findItem(R.id.action_filter);
        filteringMenuItem.setTitle("Filter = 1 : 1");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            if (num_samples == 1)  {
                num_samples = 4;
                filteringMenuItem.setTitle("Filter = 1 : 4");
            }
            else if (num_samples == 4) {
                num_samples = 10;
                filteringMenuItem.setTitle("Filter = 1 : 10");
            }
            else {
                num_samples = 1;
                filteringMenuItem.setTitle("Filter = 1 : 1");
                Toast.makeText(this, "Filtering = 1 : 1", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value_x = event.values[0] / sensorScale;
        float value_y = 0f;
        if (event.values.length > 1) {
            value_y = event.values[1] / sensorScale;
        }

        if (sampling_cycle < num_samples) {
            samples[0] = samples[0] + value_x;
            samples[1] = samples[1] + value_y;
            sampling_cycle++;
        }

        if (sampling_cycle >= num_samples) {
            sampling_cycle = 0;
            sensorXYview.redrawVector(samples[0] / (float) num_samples, samples[1] / (float) num_samples);
            samples[0] = 0f;
            samples[1] = 0f;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
