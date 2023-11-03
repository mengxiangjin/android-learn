package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Practice5DrawOvalView extends View {

    private Paint mPaint = new Paint();

    private int width = 200;
    private int height = 100;

    {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public Practice5DrawOvalView(Context context) {
        super(context);
    }

    public Practice5DrawOvalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice5DrawOvalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        练习内容：使用 canvas.drawOval() 方法画椭圆
        mPaint.setColor(Color.BLACK);
        canvas.drawOval((getWidth() - width) / 2,(getHeight() - height) / 2,(getWidth() + width) / 2,(getHeight() + height) / 2,mPaint);
    }
}
