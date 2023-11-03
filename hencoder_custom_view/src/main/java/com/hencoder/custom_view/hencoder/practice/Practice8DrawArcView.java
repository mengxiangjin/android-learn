package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Practice8DrawArcView extends View {

    private Paint mPaint = new Paint();

    private int width = 400;
    private int height = 300;

    {
        mPaint.setColor(Color.BLACK);
    }

    public Practice8DrawArcView(Context context) {
        super(context);
    }

    public Practice8DrawArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice8DrawArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        练习内容：使用 canvas.drawArc() 方法画弧形和扇形

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawArc((getWidth() - width) / 2,(getHeight() - height) / 2,(getWidth() + width) / 2,(getHeight() + height) / 2,-110,100,true,mPaint);
        canvas.drawArc((getWidth() - width) / 2,(getHeight() - height) / 2,(getWidth() + width) / 2,(getHeight() + height) / 2,10,160,false,mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc((getWidth() - width) / 2,(getHeight() - height) / 2,(getWidth() + width) / 2,(getHeight() + height) / 2,170,60,false,mPaint);
    }
}
