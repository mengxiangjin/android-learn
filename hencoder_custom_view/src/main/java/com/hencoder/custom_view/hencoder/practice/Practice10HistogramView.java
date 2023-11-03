package com.hencoder.custom_view.hencoder.practice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Practice10HistogramView extends View {

    private Paint mPaint = new Paint();

    private TextPaint textPaint = new TextPaint();
    private Path mPath = new Path();

    int orgX = 0;
    int orgY = 0;

    int width = 0;
    int offset = 20;

    List<String> list = new ArrayList<>();

    {
        list.add("Froyo");
        list.add("GB");
        list.add("ICS");
        list.add("JB");
        list.add("KitKat");
        list.add("L");
        list.add("M");

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setTextSize(24f);

    }


    public Practice10HistogramView(Context context) {
        super(context);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        orgX = 100;
        orgY = 800;

        mPath.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.moveTo(100, 100);
        mPath.lineTo(orgX, orgY);
        mPath.lineTo(1000, 800);
        width = (1000 - 100) / 7;

        canvas.drawPath(mPath, mPaint);

        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        int lineOrgX = orgX;
        int lineOrgY = orgY + 50;

        Path path = new Path();

        for (int i = 0; i < 7; i++) {
            int left = 100 + i * width + offset;
            int top = 800 - (i + 1) * 40;

            RectF rectF = new RectF(left, top, left + width - offset, 800);
            if (i == 0) {
                path.moveTo(left + (width - offset) / 2,top);

            }else {
                path.lineTo(left + (width - offset) / 2,top);

            }
            canvas.drawText(list.get(i),left + (width - offset) / 2,lineOrgY,textPaint);
            canvas.drawRect(rectF,mPaint);
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setTextSize(20f);
        canvas.drawPath(path,paint);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画直方图

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPath.reset();
        mPath.moveTo(lineOrgX,lineOrgY);
        mPath.lineTo(orgX + 900,lineOrgY);
        canvas.drawPath(mPath,mPaint);
    }
}
