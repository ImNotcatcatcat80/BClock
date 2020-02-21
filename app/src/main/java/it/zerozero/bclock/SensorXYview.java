package it.zerozero.bclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SensorXYview extends View {

    private boolean circleEnabled = true;
    private boolean traceMode = false;
    private boolean initialized = false;
    private ArrayList<CircleTrace> circleTraceArrayList = new ArrayList<CircleTrace>();
    private Paint mTextPaint, mRectPaint, mLinePaint, mCirclePaint;
    private DashPathEffect dashedLineEffect;
    private float mTouchX, mTouchY, mFirstTouchX, mFirstTouchY;
    private float vectorX, vectorY;

    public SensorXYview(Context context) {
        super(context);
        init();
    }

    public SensorXYview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SensorXYview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SensorXYview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(50f);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStrokeWidth(10);
        mRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setAlpha(128);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);
        dashedLineEffect = new DashPathEffect(new float[]{5, 5}, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int vertDivs = 8;
        int horDivs = 8;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw Background
        canvas.drawColor(Color.DKGRAY);

        // Draw axes
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setPathEffect(dashedLineEffect);
        canvas.drawLine(centerX, 0, centerX, getHeight(), mLinePaint);
        canvas.drawLine(0, centerY, getWidth(), centerY, mLinePaint);

        for (int hm = -horDivs; hm < horDivs; hm++) {
            canvas.drawLine(getWidth() / 8 * hm, centerY + 20, getWidth() / 8 * hm, centerY - 20, mLinePaint);
        }
        for (int vm = -vertDivs; vm < vertDivs; vm++) {
            canvas.drawLine(centerX - 20, getHeight() / 8 * vm, centerX + 20, getHeight() / 8 * vm, mLinePaint);
        }


        if (traceMode) {
            for(CircleTrace ct : circleTraceArrayList) {
                canvas.drawCircle(ct.getX(), ct.getY(), 20, mCirclePaint);
            }
        }

        // Draw current vector
        if (circleEnabled &! traceMode) {
            mCirclePaint.setAlpha(255);
            canvas.drawCircle(centerX, centerY, 25, mCirclePaint);
            mLinePaint.setPathEffect(null);
            mLinePaint.setStrokeWidth(1);
            // canvas.drawLine(centerX, centerY, centerX + vectorX, centerY + vectorY, mLinePaint);
            mCirclePaint.setAlpha(128);
            canvas.drawCircle(centerX + vectorX, centerY + vectorY, 50, mCirclePaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // Initialization, not necessary
        if (!initialized) {
            mTouchX = getWidth() / 2;
            mTouchY = getWidth() / 2;
            invalidate();
            initialized = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void setCircleEnabled(boolean circleEnabled) {
        this.circleEnabled = circleEnabled;
        invalidate();
    }

    public void processMotionEvent(MotionEvent ev) {
        circleEnabled = true;
        float x = ev.getX();
        float y = ev.getY();
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstTouchX = mTouchX = x;
                mFirstTouchY = mTouchY = y;
                performClick();

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_MOVE:
                mTouchX = x;
                mTouchY = y;

        }
        if (traceMode) {
            circleTraceArrayList.add(new CircleTrace((int) mTouchX, (int) mTouchY));
        }
        invalidate();
    }

    public void redrawVector(float x, float y) {

        this.vectorX = (int) (x * getWidth() / -2);
        this.vectorY = (int) (y * getHeight() / 2);
        invalidate();
    }

    public float getmTouchX() {
        return mTouchX;
    }

    public float getmTouchY() {
        return mTouchY;
    }

    public float getmFirstTouchX() {
        return mFirstTouchX;
    }

    public float getmFirstTouchY() {
        return mFirstTouchY;
    }

    public boolean isTraceMode() {
        return traceMode;
    }

    public void setTraceMode(boolean traceMode) {
        this.traceMode = traceMode;
        if (traceMode) {
            circleTraceArrayList = new ArrayList<CircleTrace>();
        }
        else {
            circleTraceArrayList = null;
        }
        mTouchX = mTouchY = mFirstTouchX = mFirstTouchY= -100;
        invalidate();
    }

    private class CircleTrace {
        int x;
        int y;

        public CircleTrace(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

}
