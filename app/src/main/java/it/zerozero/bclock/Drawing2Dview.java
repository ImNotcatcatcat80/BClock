package it.zerozero.bclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Drawing2Dview extends View {

    private boolean circleEnabled = true;
    private boolean traceMode = false;
    private boolean initialized = false;
    private ArrayList<CircleTrace> circleTraceArrayList = new ArrayList<CircleTrace>();
    private Paint mTextPaint, mRectPaint, mLinePaint, mCirclePaint;
    private DashPathEffect dashedLineEffect;
    private float mTouchX, mTouchY, mFirstTouchX, mFirstTouchY;

    public Drawing2Dview(Context context) {
        super(context);
        init();
    }

    public Drawing2Dview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Drawing2Dview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Drawing2Dview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mCirclePaint.setAlpha(192);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);
        dashedLineEffect = new DashPathEffect(new float[]{2, 6}, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("D2D", "View OD");
        int vertRects = (int) getHeight() / 100;
        int blueStep = (int) (220 / vertRects) - 1;
        int widthTot = getWidth();
        // Draw a vertical gradient
        mRectPaint.setColor(Color.rgb(20, 80, 220));
        for (int r = 0; r < vertRects; r++) {
            canvas.drawRect(0, r * 100, widthTot, r * 100 + 100, mRectPaint);
            mRectPaint.setColor(Color.rgb(20, 80, 220 - blueStep * r));
            canvas.drawLine(0, r * 100 + 100, widthTot, r * 100 + 100, mCirclePaint);
        }
        mLinePaint.setPathEffect(null);
        for (int rl = 0; rl < vertRects; rl++) {
            canvas.drawLine(0, rl * 100 + 100, widthTot, rl * 100 + 100, mLinePaint);
        }

        if (traceMode) {
            for(CircleTrace ct : circleTraceArrayList) {
                canvas.drawCircle(ct.getX(), ct.getY(), 20, mCirclePaint);
            }
        }

        if (circleEnabled &! traceMode) {
            /**
             * Just a test
             * draw red and yellow beams above / below touch circle
            RectF greyRectF = new RectF(mTouchX - 75, mTouchY - 75, mTouchX + 75, mTouchY + 75);
            mRectPaint.setColor(Color.RED);
            canvas.drawArc(greyRectF, 45, 90, true, mRectPaint);
            mRectPaint.setColor(Color.YELLOW);
            canvas.drawArc(greyRectF, 225, 90, true, mRectPaint);
            float[] points = {mTouchX - 100, mTouchY, mTouchX, mTouchY - 100, mTouchX, mTouchY - 100, mTouchX + 100, mTouchY};
            canvas.drawLines(points, mLinePaint);
            */
            canvas.drawCircle(mFirstTouchX, mFirstTouchY, 10, mCirclePaint);
            mLinePaint.setPathEffect(dashedLineEffect);
            canvas.drawLine(mFirstTouchX, mFirstTouchY, mTouchX, mTouchY, mLinePaint);
            canvas.drawCircle(mTouchX, mTouchY, 50, mCirclePaint);
            canvas.drawText(String.valueOf((int)mTouchY / 100), mTouchX, mTouchY + 20, mTextPaint);
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
