package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Practice11PieChartView extends View {


    int width = 500;

    int height = 500;

    private Paint mPaint = new Paint();
    private Paint mLinePaint = new Paint();
    private TextPaint mTextPaint = new TextPaint();
    private Path mPath = new Path();

    {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeWidth(5f);

        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(20f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public Practice11PieChartView(Context context) {
        super(context);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画饼图

        drawArcOne(canvas);
        drawArcTwo(canvas);

    }

    private void drawArcOne(Canvas canvas) {
        int left = (getWidth() - width) / 2;
        int top = (getHeight() - height) / 2;
        int right = (getWidth() + width) / 2;
        int bottom = (getHeight() + height) / 2;
        mPaint.setColor(Color.RED);
        canvas.drawArc(left, top, right, bottom, 180, 135, true, mPaint);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = width / 2;
        mPath.reset();
        double startX = centerX - (radius * Math.cos(Math.toRadians(135 / 2)));
        double startY = centerY - (radius * Math.sin(Math.toRadians(135 / 2)));
        mPath.moveTo((float) startX, (float) startY);
        mPath.lineTo((float) (startX - 50), (float) (startY - 50));
        mPath.lineTo((float) (startX - 250), (float) (startY - 50));
        canvas.drawPath(mPath, mLinePaint);
        canvas.drawText("Lollipop", (float) (startY - 50 - 100), (float) (startY - 50), mTextPaint);
    }

    private void drawArcTwo(Canvas canvas) {
        int left = (getWidth() - width) / 2 + 20;
        int top = (getHeight() - height) / 2 + 20;
        int right = (getWidth() + width) / 2 + 20;
        int bottom = (getHeight() + height) / 2 + 20;

        RectF rect = new RectF(left, top, right, bottom);
        mPaint.setColor(Color.YELLOW);
        canvas.drawArc(rect, -45, 45, true, mPaint);

        int centerX = (int) rect.centerX();
        int centerY = (int) rect.centerY();
        int radius = width / 2;
        mPath.reset();
        double startX = centerX + (radius * Math.cos(Math.toRadians(22.5)));
        double startY = centerY - (radius * Math.sin(Math.toRadians(22.5)));
        mPath.moveTo((float) startX, (float) startY);
        mPath.lineTo((float) (startX + 50), (float) (startY - 50));
        mPath.lineTo((float) (startX + 100), (float) (startY - 50));
        canvas.drawPath(mPath, mLinePaint);
        canvas.drawText("Lollipop", (float) (startY + 50 + 100), (float) (startY - 50), mTextPaint);
    }
}
