package com.jin.basic_custom_view_01.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BasicView extends View {
    public BasicView(Context context) {
        super(context);
    }

    public BasicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        //设置画笔宽度
        paint.setStrokeWidth(50);
        //设置画笔颜色
        paint.setColor(Color.RED);
        //画笔填充方式   Paint.Style.FILL， STROKE， FILL_AND_STROKE
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(200,200,100,paint);

        paint.setColor(0x7EFFFF00);
        canvas.drawCircle(200,200,50,paint);

        canvas.drawRGB(255,0,255);
        paint.setColor(Color.BLUE);
        canvas.drawLine(200,200,300,300,paint);

        canvas.drawRect(10,10,100,100,paint);
    }
}
