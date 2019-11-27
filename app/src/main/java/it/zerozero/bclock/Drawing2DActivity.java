package it.zerozero.bclock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class Drawing2DActivity extends AppCompatActivity implements Drawing2Dview.DrawingViewTouchListener {

    private TextView textViewTop;
    protected Drawing2Dview drawing2Dview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_2d);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewTop = findViewById(R.id.textViewTop);
        textViewTop.setText("ano");
        drawing2Dview = findViewById(R.id.drawind2Dview);
    }

    @Override
    public void onTouchMove(float touch_x, float touch_y) {
        textViewTop.setText(String.format(Locale.ITALIAN, "X=%.2f  Y=%.2f", touch_x, touch_y));
    }

    @Override
    public void onTouchUp(float touch_x, float touch_y) {
        Log.d("Drawing2Dview", "onTouchUp()");
        ResetCircle resetCircle = new ResetCircle();
        resetCircle.execute();
    }

    private class ResetCircle extends AsyncTask {

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
        }
    }

}
