package it.zerozero.belclock;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TerminalActivity extends AppCompatActivity {

    private TextView textViewIPAddress;
    private TextView textViewSending;
    private TextView textViewTasks;
    private Button buttonSend;
    private EditText editTextServerIP;
    private EditText editTextReceived;
    private Spinner spinnerPort;
    private ArrayList<Ports.port> portArrayList;
    private Ports.port currentPort = null;
    private static int sendTaskCounter = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    public static final int PERMISSION_INTERNET = 21880;
    public static final int PERMISSION_ACCESS_WIFI = 21770;
    public static boolean isInternetPermissionGranted = false;
    public static boolean isAccessWifiPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        textViewIPAddress = (TextView) findViewById(R.id.textViewIPAddress);
        int wifiState = WifiManager.WIFI_STATE_UNKNOWN;
        try {
            wifiState = wm.getWifiState();
        }
        catch (Exception e) {}
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            try {
                String ipAddressFrm = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                textViewIPAddress.setText("IP: " + ipAddressFrm);
            } catch (Exception e) {}
        }

        textViewSending = (TextView) findViewById(R.id.textViewSending);
        textViewTasks = (TextView) findViewById(R.id.textViewTasks);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        editTextServerIP = (EditText) findViewById(R.id.editTextServerIP);
        editTextReceived = (EditText) findViewById(R.id.editTextReceived);
        spinnerPort = (Spinner) findViewById(R.id.spinnerPort);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextReceived.setText("");
                if (currentPort != null) {
                    ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
                    clientAsyncTask.setPort(currentPort);
                    clientAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else {
                    Toast.makeText(TerminalActivity.this, "No port selected!", Toast.LENGTH_SHORT).show();
                }
                hideKeyboard();
            }
        });

        editTextReceived.setTextIsSelectable(false);
        textViewTasks.setText("Tasks: 0");

        portArrayList = new Ports().getPortsList();
        ArrayList<String> spinnerPortsArray = new ArrayList<>();
        for (Ports.port pp : portArrayList) {
            spinnerPortsArray.add(String.valueOf("  " + pp.port + "  "));
        }
        ArrayAdapter<String> portSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerPortsArray);
        spinnerPort.setAdapter(portSpinnerAdapter);
        spinnerPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentPort = portArrayList.get(i);
                textViewSending.setText("Sending: \"" + currentPort.portRequest + "\"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /** Check permissions */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
        } else {
            isInternetPermissionGranted = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_ACCESS_WIFI);
        } else {
            isAccessWifiPermissionGranted = true;
        }

        sharedPreferences = getSharedPreferences("BelClock", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        editTextServerIP.setText(sharedPreferences.getString("TerminalServerIP", ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferencesEditor.putString("TerminalServerIP", editTextServerIP.getText().toString());
        sharedPreferencesEditor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isInternetPermissionGranted = true;
            }
            else {
                isInternetPermissionGranted = false;
            }
        }
        if (requestCode == PERMISSION_ACCESS_WIFI) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isAccessWifiPermissionGranted = true;
            }
            else {
                isAccessWifiPermissionGranted = false;
            }
        }

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager iMM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public class Ports {

        public Ports() {}

        public ArrayList<port> getPortsList() {
            ArrayList<port> portsList = new ArrayList<port>();
            portsList.add(new port(21, "noop"));
            portsList.add(new port(22, "hi"));
            portsList.add(new port(23, ""));
            portsList.add(new port(80, "GET / HTTP/1.1"));
            portsList.add(new port(443, ""));
            portsList.add(new port(5037, ""));
            portsList.add(new port(5900, ""));
            portsList.add(new port(8080, "GET / HTTP/1.1"));
            return portsList;
        }

        public class port {
            private int port;
            private String portRequest;
            private String portName;

            public port(int port, String portRequest) {
                this.port = port;
                this.portRequest = portRequest;
            }
        }
    }

    public class ClientAsyncTask extends AsyncTask {

        private String reply = "";
        private Ports.port port = null;

        public void setPort (Ports.port inPort) {
            this.port = inPort;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendTaskCounter++;
            textViewTasks.setText("Tasks: " + String.valueOf(sendTaskCounter));
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            TCPClient tcpClient = new TCPClient();
            try {
                reply = tcpClient.sendReceiveStr(editTextServerIP.getText().toString(), this.port.port, this.port.portRequest);
            } finally {}
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            editTextReceived.setText(reply);
            sendTaskCounter--;
            textViewTasks.setText("Tasks: " + String.valueOf(sendTaskCounter));
        }

    }
}
