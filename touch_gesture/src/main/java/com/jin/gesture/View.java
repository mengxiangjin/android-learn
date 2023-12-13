package com.jin.gesture;

public class View {
    //事件响应先后顺序 mOnTouchListener -》 onTouchEvent -》 mOnClickListener
    //dispatchTouchEvent方法中对此3个的回调调用时机可看出

    public OnTouchListener mOnTouchListener;
    public OnClickListener mOnClickListener;

    public int left;
    public int top;
    public int right;
    public int bottom;



    public View() {
    }

    public View(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    public void setOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    //点是否在此view上
    public boolean isContainer(int x,int y) {
        boolean isContainerX = (x >= left && x <= right);
        boolean isContainerY = (y >= top && y <= bottom);
        return isContainerX && isContainerY;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;
        //1.mOnTouchListener (外部view.setOnTouchListener后 返回true 不会执行到下面的2，3处代码)
        if (mOnTouchListener != null && mOnTouchListener.onTouch(this,event)) {
            //被消费
            result = true;
        }
        //2.onTouchEvent
//        result = onTouchEvent(event);
//        if (!result) {
//            mOnClickListener.onClick(this);
//        }
        //2.mOnTouchListener.onTouch未被消费 分发给onTouchEvent
        if (!result && onTouchEvent(event)) {
            result = true;
        }
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {
        //3.onTouchEvent交给mOnClickListener
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;

    }
}
