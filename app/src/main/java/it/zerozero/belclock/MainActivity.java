package it.zerozero.belclock;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private String mSmallTextRet;
    private SharedPreferences mShPref;
    private TextView mTextViewIts;
    private TextView mTextViewSmall;
    private TextView mTextView3;
    private TextClock mTextClock;
    private TextView mTextViewAxisX;
    private TextView mTextViewBlink;
    private ProgressBar mProgBarX;
    private FloatingActionButton fab;
    private Sensor mAccelSensor;
    private SensorManager mSensorManager;
    private long mOscillatorCycle = 3500;
    private String mTimeZoneStr;
    private String mAccuracyStr;
    private String[] mTimeZoneIDs = new String[1000];
    private Handler mHandler;
    private Thread thrOscillator;
    private boolean isShowTerminal;
    private boolean isCreateNotif;
    private boolean isTransition;
    private boolean speechOverride = false;
    private final int SPEECH_REQ_CODE = 3330;
    private static final String BELCLOCK_NOTIF_CHANN = "BelClock_3039_notifChannel";
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextViewIts = (TextView) findViewById(R.id.textView);
        mTextViewSmall = (TextView) findViewById(R.id.textViewSmall);
        mTextView3 = (TextView) findViewById(R.id.textView3);
        mTextClock = (TextClock) findViewById(R.id.textClock);
        mTextViewAxisX = (TextView) findViewById(R.id.textViewAxisX);
        mTextViewBlink = (TextView) findViewById(R.id.textViewBlink);
        mProgBarX = (ProgressBar) findViewById(R.id.progressBarX);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == Oscillator.ON){
                    Log.i("Oscillator", "ON");
                    // mTextViewBlink.setVisibility(View.VISIBLE);
                    mTextViewBlink.setTextColor(Color.parseColor("#ffffff"));
                }
                else if (msg.what == Oscillator.OFF) {
                    Log.i("Oscillator", "off");
                    // mTextViewBlink.setVisibility(View.GONE);
                    mTextViewBlink.setTextColor(Color.parseColor("#000000"));
                }
            }
        };

        createNotificationChannel();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mTextClock.setOnClickListener(new View.OnClickListener() {

            private long prevClickMs = 0;
            int numClicks = 0;

            @Override
            public void onClick(View v) {

                boolean isClickFast = (System.currentTimeMillis() - prevClickMs < 500);

                prevClickMs = System.currentTimeMillis();

                if (isClickFast) {
                    numClicks++;
                    Log.i("onClick", "************************************");
                }
                else {
                    numClicks = 0;
                }

                if (numClicks > 1) {
                    numClicks = 0;

                    listenSpeech();

                    /** Per setActionButton(); da qui...
                     Intent mainIntent =new Intent("it.zerozero.belclock.MainActivity");
                     PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 4320, mainIntent,0);
                     Bitmap icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_dialer);
                     builder.setActionButton(icon, "iconDescription", pendingIntent, false);
                     ...a qui. */

                }
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String SnackBarText = "- -";
                if (mProgBarX.getVisibility() == View.GONE) {
                    mProgBarX.setVisibility(View.VISIBLE);
                    mTextViewAxisX.setVisibility(View.VISIBLE);
                    SnackBarText = "Showing Axis X value.";
                }
                else {
                    mProgBarX.setVisibility(View.GONE);
                    mTextViewAxisX.setVisibility(View.GONE);
                    SnackBarText = "Hiding Axis X value.";
                }
                Snackbar.make(view, SnackBarText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "onLongClick(...)", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return false;
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        invalidateOptionsMenu();
        isTransition = false;
        mShPref = getSharedPreferences("BelClock", MODE_PRIVATE);
        mSmallTextRet = mShPref.getString("smallText", "default return");
        mTimeZoneStr = mShPref.getString("TimeZoneStr", "default TZStr");
        isShowTerminal = mShPref.getBoolean("ShowTerminal", false);
        isCreateNotif = mShPref.getBoolean("CreateNotif", true);
        Toast.makeText(MainActivity.this, mTimeZoneStr, Toast.LENGTH_SHORT).show();
        mTextClock.setTimeZone(mTimeZoneStr);
        if (mTextClock.is24HourModeEnabled()) {
            mTextClock.setFormat24Hour("HH:mm:ss");
        }
        else {
            mTextClock.setFormat12Hour("h:mm:ss a");
        }
        String mTimeZoneGot = mTextClock.getTimeZone();
        mTextView3.setText(mTimeZoneGot);
        if (!speechOverride) {
            mTextViewSmall.setText(mSmallTextRet);
        }
        speechOverride = false;
        Log.i("mShPref", "get.");
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        long halfCycle = mOscillatorCycle / 2;
        thrOscillator = new Thread(new Oscillator(mHandler, halfCycle));
        thrOscillator.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
        Oscillator.goFlag = false;

        Intent resumeIntent = new Intent(this, MainActivity.class);
        resumeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resumeIntent, 0);

        if (isCreateNotif &! isTransition) {
            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, BELCLOCK_NOTIF_CHANN)
                    .setSmallIcon(R.drawable.ic_developer_mode_black_24px)
                    .setSound(null)
                    .setContentTitle("BelClock - onPause()")
                    .setContentText("At: " + Calendar.getInstance().getTime().toString())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setLights(0x80ffffff, 500, 2500);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(10234, notBuilder.build());  // "10234" è un id arbitrario per aggiornare poi la notifica
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationManager != null) {
            notificationManager.cancel(10234);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, 1, 1, "Led Strip Commands");
        if (isShowTerminal) {
            MenuItem mi1 = menu.add(0, 0, 0, "Item 1");
            mi1.setIcon(R.drawable.ic_computer_white_24dp);
            mi1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            isTransition = true;
            Intent optionsIntent = new Intent(this, OptionsActivity.class);
            optionsIntent.putExtra("FIRST", "Options");
            optionsIntent.putExtra("OLD_TEXT", mTextViewSmall.getText().toString());
            optionsIntent.putExtra("OLD_TZ", mTextClock.getTimeZone());
            startActivity(optionsIntent);
            return true;
        }
        if (id == R.id.action_sensors) {
            isTransition = true;
            Intent sensorsIntent = new Intent(this, SensorsActivity.class);
            startActivity(sensorsIntent);
            return true;
        }
        if (id == R.id.action_stopwatch) {
            isTransition = true;
            Intent stopwatchIntent = new Intent(this, StopwatchActivity.class);
            String timeStr = (String) mTextClock.getText();
            stopwatchIntent.putExtra("TIME_STR", timeStr);
            startActivity(stopwatchIntent);
            return true;
        }
        if (id == R.id.action_web_browser) {
            isTransition = true;
            String url = "http://www.google.com";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(000000);
            builder.setShowTitle(true);

            CustomTabsIntent customTabsIntent = builder.build();
            try {
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (id == R.id.action_info) {
            InfoDialog infoDialog = new InfoDialog();
            infoDialog.show(getSupportFragmentManager(), "info0");
        }
        if (id == 1) {
            isTransition = true;
            Intent ledActivityIntent = new Intent(this, LedStripActivity.class);
            startActivity(ledActivityIntent);
        }
        if (id == 0) {
            isTransition = true;
            Intent terminalIntent = new Intent(this, TerminalActivity.class);
            startActivity(terminalIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        speechOverride = true;
        isTransition = false;
        switch (requestCode) {
            case SPEECH_REQ_CODE:
            {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTextViewSmall.setText(result.get(0));
                    Log.i("Spoken", result.get(0));
                }
                else {
                    Log.e("onActivityResult", "...else...");
                }
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mAccuracyStr = String.valueOf(accuracy);
        Log.i("accuracy", mAccuracyStr);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float toShow = 0;
        toShow = event.values[0];
        String strToShow = String.valueOf(toShow);
        int progBarInt;
        progBarInt = (int) (4905 + 490.5 * -1 * toShow);
        mProgBarX.setProgress(progBarInt);
    }

    public void listenSpeech() {
        isTransition = true;
        Intent speakIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speakIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your clock label: ");
        try {
            startActivityForResult(speakIntent, SPEECH_REQ_CODE);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception calling speech recognition.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notif_channel_name);
            String description = getString(R.string.notif_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(BELCLOCK_NOTIF_CHANN, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
