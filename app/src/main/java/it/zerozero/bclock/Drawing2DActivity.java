package it.zerozero.bclock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Drawing2DActivity extends AppCompatActivity implements Drawing2Dview.DrawingViewTouchListener {

    private static TextView textViewTop;
    protected static Drawing2Dview drawing2Dview;
    private Menu optionsMenu;
    private MenuItem traceModeMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_2d);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewTop = findViewById(R.id.textViewTop);
        textViewTop.setText(". . . .");
        drawing2Dview = findViewById(R.id.drawind2Dview);
        drawing2Dview.setCircleEnabled(false);
        /**
        drawing2Dview.setOnTouchListener(new Drawing2Dview.DrawingViewTouchListener() {
            @Override
            public void onTouchMove(float touch_x, float touch_y) {
                Log.d("Activity onTouchMove:", String.format(Locale.ITALIAN, "X=%.1f   Y=%.1f", drawing2Dview.getmTouchX(), drawing2Dview.getmTouchY()));
            }

            @Override
            public void onTouchUp(float touch_x, float touch_y) {

            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawing2d, menu);
        optionsMenu = menu;
        traceModeMenuItem = optionsMenu.findItem(R.id.action_trace);
        traceModeMenuItem.setTitle("Trace: off");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_trace) {
            drawing2Dview.setTraceMode(!drawing2Dview.isTraceMode());
            if (drawing2Dview.isTraceMode()) {
                traceModeMenuItem.setTitle("Trace: ON");
            }
            else {
                traceModeMenuItem.setTitle("Trace: off");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onTouchMove(float touch_x, float touch_y) {
        textViewTop.setText(String.format(Locale.ITALIAN, "X=%.1f   Y=%.1f", touch_x, touch_y));
    }

    @Override
    public void onTouchUp(float touch_x, float touch_y) {
        Log.d("Drawing2Dview", "onTouchUp()");
        ResetCircle resetCircle = new ResetCircle();
        resetCircle.execute();
    }

    // TODO: 07/12/2019 Implement sharing the canvas with other apps 
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    
    private static class ResetCircle extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            drawing2Dview.setCircleEnabled(false);
            textViewTop.setText(". . . .");
        }
    }

}
