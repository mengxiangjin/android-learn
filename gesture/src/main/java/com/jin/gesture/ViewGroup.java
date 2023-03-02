package com.jin.gesture;

import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View{

    List<View> childList = new ArrayList<>();
    private View[] mChildren = new View[0];
    private String name;

    public ViewGroup(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    public void addView(View view) {
        if (view == null) {
            return;
        }
        childList.add(view);
        mChildren = childList.toArray(new View[childList.size()]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    boolean handle = false;
    //入口
    public boolean dispatchTouchEvent(MotionEvent event) {
        //super.dispatchTouchEvent() --> view.dispatchTouchEvent
       //判断是否拦截了此事件 子类可重写onInterceptTouchEvent方法（默认false不拦截）
        boolean intercept = onInterceptTouchEvent(event);
        //先处理down事件 处理完成后move事件直接找到对应的宿主即可
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && !intercept) {
            //down事件&&未被拦截 遍历子view分发事件
            //从头开始遍历还是从尾开始遍历？ --》 尾开始遍历，尾部view是最后添加的，可能会盖在前一个view的上面 手势分发到最上层的view，再层层向下分发
            for (int i = mChildren.length - 1; i >= 0; i--) {
                View childView = mChildren[i];
                //判断落地点x，y是否在此view上
                if (!childView.isContainer(event.getX(), event.getY())) {
                    continue;
                }
                //可以接受事件，分发给view (view不消费事件再回传过来到viewGroup)
                if (dispatchTransformedTouchEvent(event,childView)) {
                    //子view消费了此事件
                    handle = true;
                }
            }
        }
        //子view都没有消费此事件 (调用了super.dispatchTouchEvent(event) 自己消费)
        if (!handle) {
            dispatchTransformedTouchEvent(event,null);
        }
        return false;
    }

    //事件分发给子view
    public boolean dispatchTransformedTouchEvent(MotionEvent event,View child) {
        boolean handled = false;
        if (child != null) {
            handled = child.dispatchTouchEvent(event);
        } else {
            super.dispatchTouchEvent(event);
        }
        return handled;
    }

    //
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

}
