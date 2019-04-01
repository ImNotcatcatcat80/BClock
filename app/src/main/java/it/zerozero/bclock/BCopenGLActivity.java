package it.zerozero.bclock;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BCopenGLActivity extends AppCompatActivity {

    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOT USED
        // setContentView(R.layout.activity_bcopen_gl);

        glView = new BcGlSurfaceView(this);
        setContentView(glView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }
}
