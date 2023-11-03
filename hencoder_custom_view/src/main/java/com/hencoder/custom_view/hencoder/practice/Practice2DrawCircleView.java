package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Practice2DrawCircleView extends View {


    private Paint mPaint = new Paint();

    public Practice2DrawCircleView(Context context) {
        super(context);
    }

    public Practice2DrawCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice2DrawCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        练习内容：使用 canvas.drawCircle() 方法画圆
//        一共四个圆：1.实心圆 2.空心圆 3.蓝色实心圆 4.线宽为 20 的空心圆

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(100,100,80,mPaint);


        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(280,100,80,mPaint);

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(100,280,80,mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(20);
        canvas.drawCircle(280,280,80,mPaint);

    }
}
