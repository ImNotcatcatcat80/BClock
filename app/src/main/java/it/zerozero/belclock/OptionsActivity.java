package it.zerozero.belclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class OptionsActivity extends AppCompatActivity {

    private String smallText = "init";
    private String mOldText = "old_init";
    private String mOldTZ = "old_tz";
    private String mTimeZoneStr = "Select Time Zone";
    private boolean mShowTerminal;
    private boolean mCreateNotification;
    private RadioGroup mRadioGroupTZ;
    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private SharedPreferences mShPref;
    private SharedPreferences.Editor mPrefEditor;
    private EditText editText;
    private Switch showTerminalSwitch;
    private CheckBox checkBoxNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent inOptionsIntent = getIntent();
        // String toToast = inOptionsIntent.getStringExtra("FIRST");
        // Toast.makeText(OptionsActivity.this, toToast, Toast.LENGTH_LONG).show();
        mOldText = inOptionsIntent.getStringExtra("OLD_TEXT");
        mOldTZ = inOptionsIntent.getStringExtra("OLD_TZ");
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(mOldText);
        showTerminalSwitch = (Switch) findViewById(R.id.switch1);
        showTerminalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showTerminalSwitch.setText("Showing terminal");
                    mShowTerminal = true;
                }
                else{
                    showTerminalSwitch.setText("Hiding terminal");
                    mShowTerminal = false;
                }
            }
        });
        checkBoxNotification = (CheckBox) findViewById(R.id.checkBoxNotification);
        checkBoxNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCreateNotification = isChecked;
            }
        });
        mRadioGroupTZ = (RadioGroup) findViewById(R.id.RadioGroupTZ);
        mRadioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        mRadioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        mRadioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        mRadioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        mTimeZoneStr = mOldTZ;
        if (mOldTZ.equals("Europe/Rome")) {
            mRadioButton1.setChecked(true);
            Log.i("TZ_rb", "Europe/Rome");
        }
        else if (mOldTZ.equals("Europe/Moscow")) {
            mRadioButton2.setChecked(true);
            Log.i("TZ_rb", "Europe/Moscow");
        }
        else if (mOldTZ.equals("Asia/Tokyo")) {
            mRadioButton3.setChecked(true);
            Log.i("TZ_rb", "Asia/Tokio");
        }
        else if (mOldTZ.equals("America/New_York")) {
            mRadioButton4.setChecked(true);
            Log.i("TZ_rb", "America/New_York");
        }
        else {
            mRadioGroupTZ.clearCheck();
            Log.i("TZ_rb", "clearCheck()");
        }

        // TODO: alcune delle stringhe TimeZone sono errate (NY e Mosca...).
        {
            mRadioGroupTZ.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb0 = (RadioButton) findViewById(checkedId);
                    switch (rb0.getId()) {
                        case R.id.radioButton1:
                            mTimeZoneStr = "Europe/Rome";
                            Log.i("mRadioGroupTZ", mTimeZoneStr);
                            break;
                        case R.id.radioButton2:
                            mTimeZoneStr = "Europe/Moscow";
                            Log.i("mRadioGroupTZ", mTimeZoneStr);
                            break;
                        case R.id.radioButton3:
                            mTimeZoneStr = "Asia/Tokyo";
                            Log.i("mRadioGroupTZ", mTimeZoneStr);
                            break;
                        case R.id.radioButton4:
                            mTimeZoneStr = "America/New_York";
                            Log.i("mRadioGroupTZ", mTimeZoneStr);
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mShPref = getSharedPreferences("BelClock", MODE_PRIVATE);
        mPrefEditor = mShPref.edit();
        Log.i("mShPref", "initialized.");
        mShowTerminal = mShPref.getBoolean("ShowTerminal", false);
        showTerminalSwitch.setChecked(mShowTerminal);
        if (mShowTerminal) {
            showTerminalSwitch.setText("Showing terminal");
        }
        else {
            showTerminalSwitch.setText("Hiding terminal");
        }
        checkBoxNotification.setChecked(mShPref.getBoolean("CreateNotif", true));
    }

    @Override
    protected void onPause(){
        super.onPause();

        smallText = editText.getText().toString();

        mPrefEditor.putString("smallText", smallText);
        mPrefEditor.putString("TimeZoneStr", mTimeZoneStr);
        mPrefEditor.putBoolean("ShowTerminal", mShowTerminal);
        mPrefEditor.putBoolean("CreateNotif", mCreateNotification);
        mPrefEditor.commit();
        Log.i("mShPref", ".commit()");
    }

}
