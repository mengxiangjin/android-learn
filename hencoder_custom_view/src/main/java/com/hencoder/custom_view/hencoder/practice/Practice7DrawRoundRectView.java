package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Practice7DrawRoundRectView extends View {


    private Paint mPaint = new Paint();

    private int width = 400;
    private int height = 300;

    {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLACK);
    }

    public Practice7DrawRoundRectView(Context context) {
        super(context);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        练习内容：使用 canvas.drawRoundRect() 方法画圆角矩形

        canvas.drawRoundRect((getWidth() - width) / 2,(getHeight() - height) / 2,(getWidth() + width) / 2,(getHeight() + height) / 2,50f,50f,mPaint);
    }
}
