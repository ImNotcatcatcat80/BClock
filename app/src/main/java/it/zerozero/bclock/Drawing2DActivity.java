package it.zerozero.bclock;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Drawing2DActivity extends AppCompatActivity {

    private TextView textViewTop;
    private Drawing2Dview drawing2Dview;

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

}
