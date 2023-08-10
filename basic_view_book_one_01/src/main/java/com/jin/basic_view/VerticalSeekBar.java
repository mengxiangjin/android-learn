package com.jin.basic_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    Paint mPaint = new Paint();

    private int mThumbWidth = dp2px(60);

    private int yOffset = dp2px(5);

    private int xOffset = dp2px(25);

    private int currentProgress;



    public VerticalSeekBar(Context context) {
        super(context);
        initView();
    }
    public VerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public VerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(12));
    }


    @Override
    protected void onDraw(Canvas canvas) {


        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        String progressText = "%";
        Rect thumbBounds = getThumb().getBounds();
        canvas.drawText(progressText,(thumbBounds.left + thumbBounds.right) / 2 + mThumbWidth / 4 - xOffset ,(thumbBounds.top + thumbBounds.bottom) / 2 + yOffset,mPaint);

        super.onDraw(canvas);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredHeight();
        int height = getMeasuredWidth();
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            	//因为我这是向上滑动的，所以最大值乘Y轴的滑动比例得到上面的取值，实际值再用最大值去减即可
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
        }
        return true;
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        super.setProgress(progress);
        currentProgress = progress;
        onSizeChanged(getWidth(),getHeight(),0,0);
    }
}
