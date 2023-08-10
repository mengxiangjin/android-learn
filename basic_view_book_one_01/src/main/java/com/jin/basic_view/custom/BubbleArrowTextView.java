package com.jin.basic_view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

import com.jin.basic_view.R;


/**
 * Created by Android Studio. User: liangyongyao Date: 2021/3/7 Des: 带倒三角的气泡
 */
public class BubbleArrowTextView extends AppCompatTextView {
 
 private final static int TRIANGLE_DIRECTION_TOP = 1;
 private final static int TRIANGLE_DIRECTION_BOTTOM = 2;
 private final static int TRIANGLE_DIRECTION_LEFT = 1;
 private final static int TRIANGLE_DIRECTION_RIGHT = 2;
 
 
 private Paint mPaint;
 private Paint mStrokePaint;
 
 private int mBgColor;
 private int mStrokeColor;
 private int mStrokeWidth;
 private int mTotalHeight;
 private int mTotalWidth;
 private int mLabelHeight;
 private int mTriangleHeight;
 private int mTriangleWidth;
 private int mRadius;
 private int triangleDirection;
 
 public BubbleArrowTextView(Context context) {
 this(context, null);
 }
 
 public BubbleArrowTextView(Context context, AttributeSet attrs) {
  this(context, attrs, 0);
 }
 
 public BubbleArrowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
 super(context, attrs, defStyleAttr);
 init(context, attrs, defStyleAttr);
 }
 
 public void init(Context context, AttributeSet attrs, int defStyleAttr) {
 if (attrs != null) {
  TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleArrowTextView);
  mBgColor = a.getColor(R.styleable.BubbleArrowTextView_bubbleColor, 0);
  mStrokeColor = a.getColor(R.styleable.BubbleArrowTextView_bubbleStrokeColor, 0);
  mRadius = a.getDimensionPixelOffset(R.styleable.BubbleArrowTextView_bubbleRadius, 0);
  mStrokeWidth = a.getDimensionPixelOffset(R.styleable.BubbleArrowTextView_bubbleStrokeWidth, 0);
  mTriangleHeight = a.getDimensionPixelOffset(R.styleable.BubbleArrowTextView_triangleHeight, 6);
  mTriangleWidth = a.getDimensionPixelOffset(R.styleable.BubbleArrowTextView_triangleWidth, 3);
  triangleDirection = a.getInt(R.styleable.BubbleArrowTextView_triangleDirection, 0);
  a.recycle();
 }
 
 setGravity(Gravity.CENTER);
 initPaint();
 }
 
 //初始化画笔
 public void initPaint() {
 mPaint = new Paint();
 mPaint.setAntiAlias(true);
 mPaint.setStyle(Paint.Style.FILL);
 mPaint.setTextSize(getPaint().getTextSize());
 mPaint.setDither(true);
 }
 
 //初始化边框线画笔
 public void initStrokePaint() {
 mStrokePaint = new Paint();
 mStrokePaint.setAntiAlias(true);
 mStrokePaint.setStyle(Paint.Style.FILL);
 mStrokePaint.setDither(true);
 }
 
 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  int size = MeasureSpec.getSize(heightMeasureSpec);
  Log.d("lzy", "onMeasure: " + size);
  mLabelHeight = getFontHeight() + getPaddingTop() + getPaddingBottom();
 mTotalHeight = mLabelHeight + mTriangleHeight * 2 + mStrokeWidth * 2;
 mTotalWidth = getPaddingLeft() + getFontWidth() + getPaddingRight() + mStrokeWidth * 2;

  Log.d("lzy", "mTotalHeight: " +mTotalHeight);
  Log.d("lzy", "mTotalWidth: " +mTotalWidth);

  setMeasuredDimension(mTotalWidth, mTotalHeight);
 }
 @Override
 protected void onDraw(Canvas canvas) {
  drawView(canvas);
  getFontHeight();
  super.onDraw(canvas);

 }
 
 //绘制气泡
 private void drawView(Canvas canvas) {
 if (mStrokeColor != 0 && mStrokeWidth != 0) {
  initStrokePaint();
  mStrokePaint.setColor(mStrokeColor);
  drawRound(canvas, mStrokePaint, 0);
  drawTriangle(canvas, mStrokePaint, 0);
 }
// if (mBgColor != 0) {
//  mPaint.setColor(mBgColor);
//  drawRound(canvas, mPaint, mStrokeWidth);
//  drawTriangle(canvas, mPaint, mStrokeWidth);
// }
 }
 
 //绘制矩形
 private void drawRound(Canvas canvas, Paint paint, int strokeWidth) {
 canvas.drawRoundRect(strokeWidth, mTriangleHeight + strokeWidth,
  mTotalWidth - strokeWidth, mTotalHeight - mTriangleHeight - strokeWidth,
  mRadius, mRadius, paint);
 }
 
 //绘制三角形
 private void drawTriangle(Canvas canvas, Paint paint, int strokeWidth) {
 Path path = new Path();
 switch (triangleDirection) {
  //上
  case TRIANGLE_DIRECTION_TOP:
  path.moveTo(mTotalWidth * 0.8f - mTriangleWidth / 2 + strokeWidth / 2, mTriangleHeight + strokeWidth);
  path.lineTo(mTotalWidth * 0.8f, strokeWidth + strokeWidth / 2);
  path.lineTo(mTotalWidth * 0.8f + mTriangleWidth / 2 - strokeWidth / 2, mTriangleHeight + strokeWidth);
  break;
  //下
  case TRIANGLE_DIRECTION_BOTTOM:
   float point1x = mTotalWidth * 0.9f - mTriangleWidth/2 + strokeWidth / 2;
   float point1y = mTotalHeight - mTriangleHeight - strokeWidth;

   Log.d("lzy", "point1: " + point1x);
   Log.d("lzy", "point1: " + point1y);

   float point2x = mTotalWidth * 0.9f;
   float point2y = mTotalHeight - strokeWidth - strokeWidth / 2;
   Log.d("lzy", "point2: " + point2x);
   Log.d("lzy", "point2: " + point2y);

   float point3x = mTotalWidth * 0.9f + mTriangleWidth/2 - strokeWidth / 2;
   float point3y = mTotalHeight - mTriangleHeight - strokeWidth;

   Log.d("lzy", "point3: " + point3x);
   Log.d("lzy", "point3: " + point3y);

  path.moveTo(mTotalWidth  - mTriangleWidth + strokeWidth, mTotalHeight - mTriangleHeight
   - strokeWidth);
  path.lineTo(mTotalWidth, mTotalHeight - strokeWidth - strokeWidth / 2);
  path.lineTo(mTotalWidth, mTotalHeight - mTriangleHeight
   - strokeWidth);
  break;
  default:
  return;
 }
 canvas.drawPath(path, paint);
 }
 
 //根据字号求字体高度
 private int getFontHeight() {
 Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
  Log.d("lzy", "getFontHeight: " + Math.round(fontMetrics.descent - fontMetrics.ascent));
  Log.d("lzy", "getFontHeight: top " + fontMetrics.top);
  Log.d("lzy", "getFontHeight: bottom " + fontMetrics.bottom);
  Log.d("lzy", "getFontHeight: descent " + fontMetrics.descent);
  Log.d("lzy", "getFontHeight: ascent " + fontMetrics.ascent);
 return Math.round(fontMetrics.descent - fontMetrics.ascent);
 }
 
 //根据字号求字体宽度
 private int getFontWidth() {
  Log.d("lzy", "getFontWidth: " + mPaint.measureText(getText().toString()));

  return (int) mPaint.measureText(getText().toString());
 }
} 