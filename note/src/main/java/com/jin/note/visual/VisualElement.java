package com.jin.note.visual;

import android.view.MotionEvent;

public abstract class VisualElement {
    public abstract void initVisualElement();

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }
}
