package it.zerozero.bclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

public class Drawing2Dview extends View {

    private boolean circleEnabled = true;
    private boolean initialized = false;
    private Paint mTextPaint, mRectPaint, mLinePaint, mCirclePaint;
    private float mTouchX, mTouchY, mOldTouchX, mOldTouchY;
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
        mRectPaint.setColor(Color.rgb(24, 64, 220));
        mRectPaint.setStrokeWidth(10);
        mRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setAlpha(192);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(10);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.i("Drawing2Dview","click.");
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                circleEnabled = true;
                float x = event.getX();
                float y = event.getY();
                // Log.d("Touch X Y", String.format(Locale.ITALIAN, "X=%.2f  Y=%.2f", x, y));
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchX = x;
                        mTouchY = y;
                    case MotionEvent.ACTION_UP:
                        mOldTouchX = x;
                        mOldTouchY = y;
                    case MotionEvent.ACTION_MOVE:
                        mTouchX = x;
                        mTouchY = y;
                        mListener.onTouchDown(x, y);
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
        mRectPaint.setColor(Color.rgb(24, 64, 220));
        for (int r = 0; r < vertRects; r++) {
            canvas.drawRect(0, r * 100, widthTot, r * 100 + 100, mRectPaint);
            mRectPaint.setColor(Color.rgb(18, 64, 220 - blueStep * r));
        }

        if (circleEnabled) {
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
        mListener = (DrawingViewTouchListener) getContext();
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
        mListener = null;
    }

    public void setCircleEnabled(boolean circleEnabled) {
        this.circleEnabled = circleEnabled;
    }

    public interface DrawingViewTouchListener {
        void onTouchDown(float touch_x, float touch_y);
    }

}
