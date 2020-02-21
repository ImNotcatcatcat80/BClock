package it.zerozero.bclock;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener{

    private TextView mTextViewSensorsSpinner;
    private Spinner mSpinnerSensors;
    private TextView mTextViewSensorInfo;
    private EditText mEditTextSensorInfo;
    private SensorManager sensMgr;
    private Sensor mSelectedSensor;
    private List<Sensor> mSensorList;
    private ArrayList<String> mSensorNames;
    private Button buttonXY;
    private TextView mLabelVal0;
    private TextView mLabelVal1;
    private TextView mLabelVal2;
    private TextView mVal0;
    private TextView mVal1;
    private TextView mVal2;
    private AppSynchronizer appSynchronizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        mTextViewSensorsSpinner = (TextView) findViewById(R.id.textViewSensorsSpinner);
        mSpinnerSensors = (Spinner) findViewById(R.id.spinnerSensors);
        mTextViewSensorInfo = (TextView) findViewById(R.id.textViewSensorInfo);
        mEditTextSensorInfo = (EditText) findViewById(R.id.editTextSensorInfo);
        mLabelVal0 = (TextView) findViewById(R.id.textViewLabelVal0);
        mLabelVal1 = (TextView) findViewById(R.id.textViewLabelVal1);
        mLabelVal2 = (TextView) findViewById(R.id.textViewLabelVal2);
        mVal0 = (TextView) findViewById(R.id.textViewVal0);
        mVal1 = (TextView) findViewById(R.id.textViewVal1);
        mVal2 = (TextView) findViewById(R.id.textViewVal2);
        buttonXY = (Button) findViewById(R.id.buttonXY);
        mEditTextSensorInfo.setTextIsSelectable(false);
        mTextViewSensorInfo.setVisibility(View.GONE);
        mEditTextSensorInfo.setVisibility(View.GONE);
        mLabelVal0.setVisibility(View.GONE);
        mLabelVal1.setVisibility(View.GONE);
        mLabelVal2.setVisibility(View.GONE);
        mVal0.setVisibility(View.GONE);
        mVal1.setVisibility(View.GONE);
        mVal2.setVisibility(View.GONE);

        sensMgr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorList = sensMgr.getSensorList(Sensor.TYPE_ALL);
        mSensorNames = new ArrayList<>();  // inizializzo, se no è "null object reference".
        for (Sensor sensorToName : mSensorList) {
            mSensorNames.add(sensorToName.getName());
        }

        appSynchronizer = AppSynchronizer.getInstance();

        mEditTextSensorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(this, ". . . .", Toast.LENGTH_SHORT);
                if(mVal0.getVisibility() == View.GONE){
                    mLabelVal0.setVisibility(View.VISIBLE);
                    mLabelVal1.setVisibility(View.VISIBLE);
                    mLabelVal2.setVisibility(View.VISIBLE);
                    mVal0.setVisibility(View.VISIBLE);
                    mVal1.setVisibility(View.VISIBLE);
                    mVal2.setVisibility(View.VISIBLE);
                }
                else{
                    mLabelVal0.setVisibility(View.GONE);
                    mLabelVal1.setVisibility(View.GONE);
                    mLabelVal2.setVisibility(View.GONE);
                    mVal0.setVisibility(View.GONE);
                    mVal1.setVisibility(View.GONE);
                    mVal2.setVisibility(View.GONE);
                }
            }
        });

        ArrayAdapter<String> sensorArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mSensorNames);
        mSpinnerSensors.setAdapter(sensorArrayAdapter);
        mSpinnerSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sensor selectedSensor = mSensorList.get(mSpinnerSensors.getSelectedItemPosition());
                String selectedSensorStr = selectedSensor.toString();
                Log.i("selectedSensor", selectedSensorStr);
                mTextViewSensorInfo.setVisibility(View.VISIBLE);
                mEditTextSensorInfo.setVisibility(View.VISIBLE);
                mEditTextSensorInfo.setText(selectedSensorStr);
                mSelectedSensor = selectedSensor;
                // Register listener in order to show in Val[0..4] values of selected sensor
                registerSensLst();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTextViewSensorInfo.setVisibility(View.GONE);
                mEditTextSensorInfo.setVisibility(View.GONE);
                mEditTextSensorInfo.setText("");
            }
        });
        buttonXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sensorXYintent = new Intent(getApplicationContext(), SensorXYactivity.class);
                appSynchronizer.setSensor(mSelectedSensor);
                startActivity(sensorXYintent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        registerSensLst();
    }

    @Override
    public void onPause(){
        super.onPause();
        sensMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            float v0 = (float) Math.round(event.values[0] * 100) / 100;
            float v1 = (float) Math.round(event.values[1] * 100) / 100;
            float v2 = (float) Math.round(event.values[2] * 100) / 100;
            mVal0.setText(String.valueOf(v0));  // mVal0.setText(String.valueOf(event.values[0]));
            mVal1.setText(String.valueOf(v1));  // mVal1.setText(String.valueOf(event.values[1]));
            mVal2.setText(String.valueOf(v2));  // mVal2.setText(String.valueOf(event.values[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensLst(){
        Log.i("registerSensLst", "...");
        try {
            sensMgr.unregisterListener(this);
            String selectedSensorStr = mSelectedSensor.toString();
            Log.i("mSelectedSensor", selectedSensorStr);
            sensMgr.registerListener(this, mSelectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
