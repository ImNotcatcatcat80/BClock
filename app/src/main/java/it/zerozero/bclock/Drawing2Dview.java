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

public class Drawing2Dview extends View {

    private boolean circleEnabled = true;
    private boolean traceMode = false;
    private boolean initialized = false;
    private ArrayList<CircleTrace> circleTraceArrayList = new ArrayList<CircleTrace>();
    private Paint mTextPaint, mRectPaint, mLinePaint, mCirclePaint;
    private DashPathEffect dashedLineEffect;
    private float mTouchX, mTouchY, mFirstTouchX, mFirstTouchY;
    private DrawingViewTouchListener mListener;

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

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                circleEnabled = true;
                float x = event.getX();
                float y = event.getY();
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFirstTouchX = mTouchX = x;
                        mFirstTouchY = mTouchY = y;
                        Drawing2Dview.super.performClick();
                    case MotionEvent.ACTION_UP:
                        mListener.onTouchUp(x, y);
                    case MotionEvent.ACTION_MOVE:
                        mTouchX = x;
                        mTouchY = y;
                        mListener.onTouchMove(x, y);
                }
                invalidate();
                return Drawing2Dview.super.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a vertical gradient
        int vertRects = (int) getHeight() / 100;
        int blueStep = (int) (220 / vertRects) - 1;
        int widthTot = getWidth();
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

        }

        if (circleEnabled) {
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
        // mListener = (DrawingViewTouchListener) getContext();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mListener = (DrawingViewTouchListener) getContext();

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
        mListener = null;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void setCircleEnabled(boolean circleEnabled) {
        this.circleEnabled = circleEnabled;
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
    }

    public void setOnTouchListener(DrawingViewTouchListener drawingViewTouchListener) {
        // Not needed, as mListener is already set in onAttachedToWindow
        mListener = (DrawingViewTouchListener) getContext();
    }

    public interface DrawingViewTouchListener {
        void onTouchMove(float touch_x, float touch_y);
        void onTouchUp(float touch_x, float touch_y);
    }

    private class CircleTrace {
        int x;
        int y;

        public CircleTrace(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
