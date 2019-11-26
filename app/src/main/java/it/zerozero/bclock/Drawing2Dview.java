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

import java.util.Locale;

public class Drawing2Dview extends View {

    private float mWidth;
    private float mHeight;
    private Paint mTextPaint;
    private Paint mRectPaint;
    private float mOldTouchX, mOldTouchY;

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
        mTextPaint.setTextSize(40f);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.rgb(24, 64, 220));
        mRectPaint.setStrokeWidth(10);
        mRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Drawing2Dview","click.");
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX() - getLeft();
                float y = event.getY() - getTop();
                Log.d("Touch X Y", String.format(Locale.ITALIAN, "X=%.2f  Y=%.2f", x, y));
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mOldTouchX = x;
                        mOldTouchY = y;
                }
                invalidate();
                return Drawing2Dview.super.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mOldTouchX-50, mOldTouchY-50, 100, 100, mRectPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

}
