package it.zerozero.bclock;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;

public class LedStripActivity extends AppCompatActivity {

    private NumberPicker numberPickerR;
    private NumberPicker numberPickerG;
    private NumberPicker numberPickerB;
    private EditText editTextLedStripIP;
    private TextView textViewLabelLedStripDevice;
    private static TextView textViewLedStripStatus;
    private SeekBar seekBarLeds;
    private LedStripView ledStripView;
    private Switch switchReverseLedStrip;
    private Switch switchBT;
    private Switch switchNSD;
    private TextView textViewLedNo;
    private static final int PERMISSION_BLUETOOTH = 30030;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 30033;
    private static final int PERMISSION_LOCATION_COARSE = 30040;
    private static final int REQUEST_ENABLE_BLUETOOTH = 30048;
    private boolean bluetoothPermissionGranted = false;
    private boolean bluetoothAdminPermissionGranted = false;
    private boolean locationCoarsePermissionGranted = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private LedStripCommands ledStripCommands;
    private Runnable updateComms;
    private Handler updateCommsHnd;
    private boolean ledStripReversed = false;
    private NsdHelper nsdHelper;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_strip);

        numberPickerR = findViewById(R.id.numberPickerR);
        numberPickerG = findViewById(R.id.numberPickerG);
        numberPickerB = findViewById(R.id.numberPickerB);
        editTextLedStripIP = findViewById(R.id.editTextLedStripIP);
        textViewLabelLedStripDevice = findViewById(R.id.textViewLabelLedStripIP);
        textViewLedStripStatus = findViewById(R.id.textViewLedStripStatus);
        seekBarLeds = findViewById(R.id.seekBarLeds);
        seekBarLeds.setVisibility(View.GONE);
        ledStripView = findViewById(R.id.ledStripView);
        textViewLedNo = findViewById(R.id.textViewLedNo);
        textViewLedNo.setVisibility(View.INVISIBLE);
        switchBT = findViewById(R.id.switchBT);
        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    boolean btPermissionsOk = checkBtPermissions();
                    Log.i("BT permGranted", String.valueOf(bluetoothPermissionGranted));
                    Log.i("BT Admin permGranted", String.valueOf(bluetoothAdminPermissionGranted));
                    if (btPermissionsOk) {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Log.e("mBluetoothAdapter", "is null.");
                        }
                        else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                            }
                            else {
                                Log.i("mBluetoothAdapter", "enabled.");
                            }
                        }
                    }
                    else {
                        switchBT.setChecked(false);
                    }
                }
            }
        });
        switchReverseLedStrip = findViewById(R.id.switchReverseLedStrip);
        switchReverseLedStrip.setVisibility(View.GONE);
        switchReverseLedStrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ledStripReversed = isChecked;
            }
        });
        switchNSD = findViewById(R.id.switchNSD);
        switchNSD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    nsdHelper = new NsdHelper(getApplicationContext());
                    nsdHelper.initializeDiscoveryListener();
                    nsdHelper.initializeResolveListener();
                    nsdHelper.startDiscovery();
                }
                else {
                    if(nsdHelper != null) {
                        nsdHelper.stopDiscovery();
                    }
                }
            }
        });

        numberPickerR.setMinValue(0);
        numberPickerG.setMinValue(0);
        numberPickerB.setMinValue(0);
        numberPickerR.setMaxValue(31);
        numberPickerG.setMaxValue(31);
        numberPickerB.setMaxValue(31);
        seekBarLeds.setEnabled(true);
        seekBarLeds.setMax(7);
        seekBarLeds.setProgress(0);

        updateCommsHnd = new Handler();
        updateComms = new Runnable() {
            @Override
            public void run() {
                ledStripCommands = new LedStripCommands();
                int r = numberPickerR.getValue();
                int g = numberPickerG.getValue();
                int b = numberPickerB.getValue();
                ledStripView.setmLedOnColor(r, g, b);
                int[] colorsAr = ledStripView.getLedColorsAr(); // new int[ledStripCommands.LEDSTRIP_LENGTH];
                ledStripCommands.setLedColorsAr(colorsAr);
                SendLedCommands sendLedCommands = new SendLedCommands();
                sendLedCommands.setData(editTextLedStripIP.getText().toString(), 19881);
                sendLedCommands.execute(ledStripCommands);
                if (!ledStripReversed) {
                    textViewLedNo.setText(String.format(Locale.ITALIAN, "LEDs: %d", seekBarLeds.getProgress()));
                } else {
                    textViewLedNo.setText(String.format(Locale.ITALIAN, "LEDs: %d", 7 - seekBarLeds.getProgress()));
                }
                Log.i("updateComms", "run()");
                updateCommsHnd.postDelayed(this, 1000);
            }
        };
    }

    private static void setStatusText(String status) {
        textViewLedStripStatus.setText(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCommsHnd.post(updateComms);
        sharedPreferences = getSharedPreferences("BelClock", MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        editTextLedStripIP.setText(sharedPreferences.getString("ledStripIP", "0.0.0.0"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCommsHnd.removeCallbacks(updateComms);
        preferencesEditor.putString("ledStripIP", editTextLedStripIP.getText().toString());
        preferencesEditor.commit();
    }

    boolean checkBtPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        }
        else {
            bluetoothPermissionGranted = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        }
        else {
            bluetoothAdminPermissionGranted = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_COARSE);
        }
        else {
            locationCoarsePermissionGranted = true;
        }
        return bluetoothPermissionGranted && bluetoothAdminPermissionGranted && locationCoarsePermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            if (requestCode == PERMISSION_BLUETOOTH) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothPermissionGranted = true;
                }
                else {
                    bluetoothPermissionGranted = false;
                    Log.e("bluetoothPermission", "NOT GRANTED.");
                }
            }
            if (requestCode == PERMISSION_BLUETOOTH_ADMIN) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothAdminPermissionGranted = true;
                }
                else {
                    bluetoothAdminPermissionGranted = false;
                    Log.e("bluetoothAdminPerm", "NOT GRANTED.");
                }
            }
            if (requestCode == PERMISSION_LOCATION_COARSE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationCoarsePermissionGranted = true;
                }
                else {
                    locationCoarsePermissionGranted = false;
                    Log.e("locationCoarsePerm", "NOT GRANTED.");
                }
            }
        }
    }

    static class SendLedCommands extends AsyncTask {

        private String ip;
        private int port;
        private String reply;

        protected void setData(String setIp, int setPort) {
            ip = setIp;
            port = setPort;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // textViewLedStripStatus.setText(R.string.LedStrip_conn_wait);
            setStatusText("..wait..");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(objects[0] instanceof LedStripCommands == false) {
                Log.e("SendLedCommands", "incompatible types");
                reply = "error";
            }
            else {
                Gson gson = new Gson();
                String JSONStr = gson.toJson(objects[0]);
                try {
                    TCPClient tcpClient = new TCPClient();
                    reply = tcpClient.sendReceiveStr(ip, port, JSONStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return reply;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(reply.equals("<ConnectedToLedStripDevice>")) {
                // textViewLedStripStatus.setText(R.string.LedStrip_conn_connected);
                setStatusText("connected");
            }

        }
    }

}
