package it.zerozero.bclock;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {

    private String timeStr;
    private long startTime;
    private String currentDisplayStr = "0.0";
    private String currentReadingStr = "0.000";
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private boolean isRunning = false;
    private Chronometer chronometer0;
    private Button buttonPop;
    private Button buttonStop;
    private ListView readingListView;
    private ArrayList<String> readingList;
    private ArrayAdapter<String> readingListAdapter;
    private ReadingAdapter readingAdapter;
    private ReadingBaseAdapter readingBaseAdapter;
    private GSon_Serializer gSonSerializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        Intent inStopWatchIntent = getIntent();
        gSonSerializer = new GSon_Serializer(getApplicationContext(), "chronoReadings.dat");
        timeStr = inStopWatchIntent.getStringExtra("TIME_STR");
        buttonPop = (Button) findViewById(R.id.buttonPop);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        readingList = new ArrayList<>();
        readingListAdapter = new ArrayAdapter<String>(this, R.layout.reading_line, readingList);

        readingListView = (ListView) findViewById(R.id.readingListView);
        readingListView.setAdapter(readingListAdapter);

        // TODO: 18/09/2017 Implement and set ReadingBaseAdapter, then replace add/pop method with the customized ones
        // readingBaseAdapter = new ReadingBaseAdapter();
        // readingListView.setAdapter(readingBaseAdapter);

        readingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedReading = readingList.get(position);
                Toast.makeText(StopwatchActivity.this, selectedReading, Toast.LENGTH_SHORT).show();
                readingListView.setSelection(position);
            }
        });
        chronometer0 = (Chronometer) findViewById(R.id.chronometer0);
        chronometer0.setText("00 : 00");
        chronometer0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! isRunning) {      // Start or...
                    chronometer0.setBase(SystemClock.elapsedRealtime());
                    chronometer0.start();
                    isRunning = true;
                    Log.i("isRunning", String.valueOf(isRunning));
                }
                else{       // ...take reading.
                    long mins, secs, millis;
                    mins = getChronoTime(chronometer0, true)[2];
                    secs = getChronoTime(chronometer0, true)[1];
                    millis = getChronoTime(chronometer0, true)[0];
                    currentDisplayStr = String.format(Locale.ITALIAN, "%02d", mins) + " : " + String.format(Locale.ITALIAN, "%02d", secs);
                    currentReadingStr = String.format(Locale.ITALIAN, "%02d", mins) + " : " + String.format(Locale.ITALIAN, "%02d", secs) + "." + String.format(Locale.ITALIAN, "%03d", millis);
                    chronometer0.setText(currentDisplayStr);
                    Log.i("isRunning", String.valueOf(isRunning));
                    try {
                        readingList.add(currentReadingStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chronometer0.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long mins, secs;
                long[] gotReading = {0, 0, 0};
                gotReading = getChronoTime(chronometer0, true);
                mins = gotReading[2];
                secs = gotReading[1];
                currentDisplayStr = String.format(Locale.ITALIAN, "%02d", mins) + " : " + String.format(Locale.ITALIAN, "%02d", secs);
                chronometer0.setText(currentDisplayStr);
            }
        });

        buttonStop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {    // Stop
                if (isRunning && true) {
                    chronometer0.stop();
                    long[] gotReading = {0, 0, 0};
                    long mins, secs, millis;
                    gotReading = getChronoTime(chronometer0, true);
                    mins = gotReading[2];
                    secs = gotReading[1];
                    millis = gotReading[0];
                    if(gotReading[1] < 1){
                        try {
                            throw new ReadingArrayException();
                        } catch (ReadingArrayException e) {
                            e.printStackTrace();
                            Toast.makeText(StopwatchActivity.this, "Less than a second!...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    currentReadingStr = String.format(Locale.ITALIAN, "%02d", mins) + " : " + String.format(Locale.ITALIAN, "%02d", secs) + "." + String.format(Locale.ITALIAN, "%03d", millis);
                    chronometer0.setText(currentReadingStr);
                    isRunning = false;
                    Log.i("isRunning", String.valueOf(isRunning));
                }
                return false;
            }
        });

        buttonPop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                readingList.clear();
                readingListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        buttonPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sz = readingList.size();
                if (sz >= 1) {
                    readingList.remove(sz -1);
                    Toast.makeText(StopwatchActivity.this, "Popped reading # " + String.valueOf(sz), Toast.LENGTH_SHORT).show();
                }
                readingListAdapter.notifyDataSetChanged();
            }
        });

        Log.i("timeStr", timeStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        chronometer0.setText("***");
        try {
            gSonSerializer.save(readingList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Leaving chronometer", "started at " + timeStr);
    }

    @Override
    public void onResume(){
        super.onResume();
        ArrayList<String> retrievedArrayList = new ArrayList();
        try {
            retrievedArrayList = gSonSerializer.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readingList.clear();
        for (String item : retrievedArrayList){
            readingList.add(item);
        }

    }

    private long[] getChronoTime (Chronometer chr, boolean endReading) {
        long[] retReading = {0, 0, 0};
        long currentReadingLong = SystemClock.elapsedRealtime() - chr.getBase();
        retReading[2] = (currentReadingLong / 1000) / 60;
        retReading[1] = (currentReadingLong / 1000) % 60;
        retReading[0] = currentReadingLong % 1000;
        return retReading;
    }

    // TODO: 08/02/2017 implement custom adapter to use with reading_item.xml
    public class ReadingAdapter extends ArrayAdapter<String> {

        public ReadingAdapter(Context context, int resource) {
            super(context, resource);

        }

    }

    public class ReadingBaseAdapter extends BaseAdapter /* Not used*/ {

        @Override
        public Object getItem(int position) {
            return readingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reading_item, parent, false);
            }
            TextView letterTextView = (TextView) findViewById(R.id.letterTextView);
            TextView readingTextView = (TextView) findViewById(R.id.readingTextView);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            String itemStr = readingList.get(position);

            if (letterTextView != null && readingTextView != null && imageView != null) {
                letterTextView.setText("A");
                readingTextView.setText(itemStr);
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                Log.e("ReadingBaseAdapter", "something == null");
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return readingList.size();
        }

        // TODO: 18/09/2017 Implement custom methods to add/remove items from readingList
        // TODO: 19/09/2017 Use these methods in the code instead of the old ones
        public void addReading(String inStr) {
            readingList.add(inStr);
            notifyDataSetChanged();
        }

        public void popReading() {
            readingList.remove(readingList.size() - 1);
            notifyDataSetChanged();
        }

    }

    public class ReadingArrayException extends Throwable {
        // TODO: 04/04/2017 write exception and throw it when needed.

        public ReadingArrayException() {
            String contextStr = getApplicationContext().toString();
            Log.e("Less than a second", contextStr);
        }

    }

}
