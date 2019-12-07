package it.zerozero.bclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LedStripView extends View {

    private int mHeight, mWidth, mPadding, mLedRadius, mLedStep;
    private Paint mLedPaint, mLedBorderPaint;
    private int mLedOnColor;
    private int[] ledColorsAr = new int[] {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
    private float mTouchX, mTouchY;

    public LedStripView(Context context) {
        super(context);
        setup();
    }

    public LedStripView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public LedStripView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public LedStripView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setup() {
        mLedOnColor = Color.BLACK;
        mLedBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLedBorderPaint.setColor(Color.BLACK);
        mLedBorderPaint.setStyle(Paint.Style.STROKE);
        mLedBorderPaint.setStrokeWidth(4);
        mLedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLedPaint.setColor(Color.BLACK);
        mLedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLedPaint.setStrokeWidth(1);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    int ledNr = (int) (x - mPadding + mLedStep / 2) / mLedStep;
                    if (ledColorsAr[ledNr] == mLedOnColor) {
                        ledColorsAr[ledNr] = Color.BLACK;
                    }
                    else {
                        ledColorsAr[ledNr] = mLedOnColor;
                    }
                }
                invalidate();
                return LedStripView.super.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        // canvas.drawColor(Color.argb(0, 255, 255, 255));    // same; alpha == 0
        for(int led = 0; led < LedStripCommands.LEDSTRIP_LENGTH; led++) {
            int tempColor = ledColorsAr[led];
            // int tempA = (tempColor >> 24) & 0xff; // or color >>> 24
            int tempR = (tempColor >> 16) & 0xff;
            int tempG = (tempColor >>  8) & 0xff;
            int tempB = (tempColor) & 0xff;
            // offset the components to have a better representation on screen
            int r = 100 + 5 * tempR;
            int g = 100 + 5 * tempG;
            int b = 100 + 5 * tempB;
            mLedPaint.setColor(Color.rgb(r, g, b));
            canvas.drawCircle(mPadding + led * mLedStep, mHeight / 2, mLedRadius, mLedPaint);
            canvas.drawCircle(mPadding + led * mLedStep, mHeight / 2, mLedRadius, mLedBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        mPadding = getWidth() / 8;
        mLedStep = (getWidth() - 2 * mPadding) / 6;
        mLedRadius = 36;
        Log.d("mLedRadius", String.valueOf(mLedRadius));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int[] getLedColorsAr() {
        // ledColorsAr is returned reversed
        int[] outArray = new int[LedStripCommands.LEDSTRIP_LENGTH];
        for (int n = 0; n < LedStripCommands.LEDSTRIP_LENGTH; n++) {
            outArray[6 - n] = ledColorsAr[n];
        }
        return outArray;
    }

    public void setmLedOnColor(int r, int g, int b) {
        this.mLedOnColor = Color.rgb(r, g, b);
    }
}
