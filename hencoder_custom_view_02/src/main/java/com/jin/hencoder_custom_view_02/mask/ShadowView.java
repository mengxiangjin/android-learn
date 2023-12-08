package com.jin.hencoder_custom_view_02.mask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jin.hencoder_custom_view_02.R;

public class ShadowView extends View {
 
    Paint pRect = new Paint();
    private Rect mRect = new Rect();
    private View mView;
    private int[] location = new int[2];
 
    public ShadowView(Context context) {
        super(context);
        pRect.setColor(Color.RED);
        pRect.setAntiAlias(true);
    }
 
    public ShadowView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }
 
    public ShadowView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
 
    /**
     * 引导区域View
     *
     * @param view
     */
    public void setView(View view) {
        mView = view;
    }
 
    /**
     * 设置引导区域
     *
     * @param rect
     */
    public void setRect(Rect rect) {
        mRect = rect;
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
 
        if (mView != null) {
            mView.getLocationInWindow(location);
            mRect.left = location[0];
            mRect.top = location[1];
            mRect.right = location[0] + mView.getMeasuredWidth();
            mRect.bottom = location[1] + mView.getMeasuredHeight();
        }
 
        if (mRect != null) {
            canvas.drawRect(0, 0, mRect.right, mRect.bottom, pRect);
//            canvas.drawRect(0, mRect.bottom, getWidth(), getHeight(), pRect);
//            canvas.drawRect(0, mRect.top, mRect.left, mRect.bottom, pRect);
//            canvas.drawRect(mRect.right, mRect.top, getWidth(), mRect.bottom, pRect);
        }
    }
}