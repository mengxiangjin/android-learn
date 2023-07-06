package com.jin.weather_pager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import com.jin.weather_pager.utils.SVUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherLineChartView extends View {

    private List<Integer> yValues;

    private List<Point> pointList;
    private Paint paintLines, paintSmallCircle, paintBigCircle,paintSelectedBg,paintText;

    private Integer currentSelectPosition;

    private int maxYValue;
    private int minYValue;

    private int topMargin = SVUtils.dip2px(getContext(),10);

    private int maxYoffset = SVUtils.dip2px(getContext(),10);
    private int bottomMargin = SVUtils.dip2px(getContext(),10);

    private int smallCircleRadius = SVUtils.dip2px(getContext(),2);

    private int bigCircleRadius = SVUtils.dip2px(getContext(),5);


    public WeatherLineChartView(Context context) {
        this(context, null);
    }

    public WeatherLineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherLineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pointList = new ArrayList<>();
        initPaint();
    }

    private void initPaint() {
        paintLines = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLines.setColor(Color.WHITE);
        paintLines.setStyle(Paint.Style.STROKE);
        paintLines.setStrokeWidth(2);

        paintSmallCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSmallCircle.setColor(Color.WHITE);
        paintSmallCircle.setStrokeWidth(3f);
        paintSmallCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        paintBigCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBigCircle.setColor(Color.WHITE);
        paintBigCircle.setStrokeWidth(3f);
        paintBigCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        paintSelectedBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSelectedBg.setColor(Color.WHITE);
        paintSelectedBg.setStrokeWidth(3f);
        paintSelectedBg.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSelectedBg.setAlpha(76);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setStrokeWidth(3f);
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(SVUtils.sp2Px(getContext(),12));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPoints(canvas);
        drawLine(canvas);
    }


    private void drawPoints(Canvas canvas) {
        if (yValues == null || yValues.isEmpty() || yValues.size() < 5) return;
        maxYValue = yValues.get(0);
        minYValue = yValues.get(0);
        for (int i = 0; i < yValues.size(); i++) {
            // 找出y轴的最大最小值
            if (yValues.get(i) > maxYValue) {
                maxYValue = yValues.get(i);
            }
            if (yValues.get(i) < minYValue) {
                minYValue = yValues.get(i);
            }
        }
        pointList.clear();
        int realHeight = getHeight() - topMargin - bottomMargin;
        int yStep = realHeight / (maxYValue - minYValue);
        int xStep =  getWidth() / yValues.size();
        for (int i = 0; i < yValues.size(); i++) {
            Integer temperature = yValues.get(i);
            int currentX = xStep / 2 + i * xStep;
            int currentY = realHeight - (temperature - minYValue) * yStep;
            //留出一定空间绘制
            if (temperature == maxYValue) {
                currentY += maxYoffset;
            }
            Point point = new Point(currentX, currentY);
            pointList.add(point);
            if (currentSelectPosition == i) {
                canvas.drawCircle(currentX,currentY,bigCircleRadius, paintBigCircle);
                float left = currentX + bigCircleRadius + SVUtils.dip2px(getContext(),5);
                float top = currentY - bigCircleRadius - SVUtils.dip2px(getContext(),1);
                float right = left + SVUtils.dip2px(getContext(),50);
                float bottom = top + SVUtils.dip2px(getContext(),28);
                float concern =  SVUtils.dip2px(getContext(),25);
                if (currentSelectPosition == yValues.size() - 1) {
                    left = currentX - bigCircleRadius - SVUtils.dip2px(getContext(),5) - right + left;
                    right = left + SVUtils.dip2px(getContext(),50);
                }
                RectF rectF = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rectF,concern,concern,paintSelectedBg);
                canvas.drawText("+" + temperature +"℃",rectF.centerX(),rectF.centerY() + 10,paintText);
            } else {
                canvas.drawCircle(currentX,currentY,smallCircleRadius, paintSmallCircle);
            }
        }
    }


    private void drawLine(Canvas canvas) {
        if (pointList == null || pointList.isEmpty()) return;
        Point srcPoint = pointList.get(0);
        Path path = new Path();
        path.moveTo(srcPoint.x,srcPoint.y);
        for (int i = 0; i < pointList.size(); i++) {
            int j = i + 1;
            if (j != pointList.size()) {
                Point nextPoint = pointList.get(j);
                path.lineTo(nextPoint.x,nextPoint.y);
            }
        }
        canvas.drawPath(path,paintLines);
    }

    public List<Integer> getyValues() {
        return yValues;
    }

    public void setyValues(List<Integer> yValues) {
        this.yValues = yValues;
    }

    public int getCurrentSelectPosition() {
        return currentSelectPosition;
    }

    public void setCurrentSelectPosition(Integer currentSelectPosition) {
        this.currentSelectPosition = currentSelectPosition;
        invalidate();
    }
}