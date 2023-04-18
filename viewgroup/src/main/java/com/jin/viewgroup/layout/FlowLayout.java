package com.jin.viewgroup.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {


    private int mHorizontalSpace = dp2px(16);   //item横向间距
    private int mVerticalSpace = dp2px(8);   //item纵向间距


    private List<List<View>> allLines;  //记录所有的行 一行一行layout 用于layout
    private List<Integer> lineHeights;  //记录每一行的行高 用于layout

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    * 计算每个子view的宽高后，最后再度量自己
    *
    * MeasureSpec int类型的值
    * 前二位model:     UNSPECIFIED：大小无限制  EXACTLY：确切值 AT_MOST：大小不能超过某个值 如wrap content
    * 后三十未size
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        allLines = new ArrayList<>();
        lineHeights = new ArrayList<>();
        //度量所有的子view
        int childCount = getChildCount();


        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();


        //保存一行的views
        List<View> lineViews = new ArrayList<>();
        int lineWidthUsed = 0;  //记录一行已经使用过的width判断是否需要换行
        int lineHeight = 0; //记录一行的 最大宽度

        //viewgroup自己通过父亲测量所得的宽度、高度 判断摆放view是否需要换行
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        int parentNeedWidth = 0;
        int parentNeedHeight = 0;


        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            LayoutParams childLp = childView.getLayoutParams();
            //需通过viewgroup度量子view  父容器的度量宽，父容器的padding，子view所需要的空间
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLp.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLp.height);
            //开始度量
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);

            //拿到每个子view的度量宽高 用于度量父亲
            int childMeasuredWidth = childView.getMeasuredWidth();
            int childMeasuredHeight = childView.getMeasuredHeight();

            //是否需要换行
            if (childMeasuredWidth + lineWidthUsed + mHorizontalSpace > selfWidth) {
                //换行 需记录高度、宽度
                parentNeedWidth = Math.max(parentNeedWidth,lineWidthUsed + mHorizontalSpace);
                parentNeedHeight = parentNeedHeight + lineHeight + mVerticalSpace;

                allLines.add(lineViews);
                lineHeights.add(lineHeight);

                lineViews = new ArrayList<>();
                lineWidthUsed = 0;
                lineHeight = 0;

                if (i == childCount - 1) {
                    allLines.add(lineViews);
                    lineHeights.add(lineHeight);
                    parentNeedWidth = Math.max(parentNeedWidth,lineWidthUsed + mHorizontalSpace);
                    parentNeedHeight = parentNeedHeight + lineHeight + mVerticalSpace;
                }
            }

            lineViews.add(childView);
            //每行都有自己的宽度和高度
            lineWidthUsed = childMeasuredWidth + mHorizontalSpace + lineWidthUsed;
            lineHeight = Math.max(lineHeight,childMeasuredHeight);
        }

        //度量和保存自己   子view最宽的即为我自己宽度 子view高度之和即我所需的高度
        //自己的度量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //度量自己
        int realWidth = (widthMode == MeasureSpec.EXACTLY)? selfWidth: parentNeedWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY)? selfHeight: parentNeedHeight;

        setMeasuredDimension(realWidth,realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int curL = getPaddingLeft();
        int curT = getPaddingTop();
        //遍历所有的子view进行layout
        for (int i = 0; i < allLines.size(); i++) {
            List<View> views = allLines.get(i);
            int height = lineHeights.get(i);
            for (int j = 0; j < views.size(); j++) {
                View view = views.get(j);
                int left = curL;
                int top = curT;
                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();

                view.layout(left,top,right,bottom);
                curL = right + mHorizontalSpace;
            }
            //下一行 更新
            curL = getPaddingLeft();
            curT = curT + height + mVerticalSpace;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public static int dp2px(int dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, Resources.getSystem().getDisplayMetrics());
    }

}
