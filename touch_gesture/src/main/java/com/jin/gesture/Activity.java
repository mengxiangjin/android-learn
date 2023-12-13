package com.jin.gesture;

public class Activity {


    public static void main(String[] args) {
        MotionEvent event = new MotionEvent(100, 100);
        event.setActionMasked(MotionEvent.ACTION_DOWN);

        dispatchTouchEvent(event);
    }

    public static boolean dispatchTouchEvent(MotionEvent event) {
        ViewGroup viewGroup = new ViewGroup(0, 0, 1080, 1920) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return true;
            }
        };
        viewGroup.setName("顶级容器");
        viewGroup.setOnTouchListener(new OnTouchListener() {
            //返回值对分发影响很大
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                System.out.println("viewgroup setOnTouchListener");
                return false;
            }
        });

        viewGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("viewgroup setOnClickListener");
            }
        });

        View view = new View(0, 0, 200, 200);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("-------onClick-------");
            }
        });
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                System.out.println("---------onTouch-------");
                return false;
            }
        });
        viewGroup.addView(view);
        //事件分发给viewgroup
        viewGroup.dispatchTouchEvent(event);
        return true;
    }
}
