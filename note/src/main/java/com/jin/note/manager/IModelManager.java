package com.jin.note.manager;

import android.view.MotionEvent;

import com.jin.note.doodle.ITouchEventListener;

import org.jetbrains.annotations.Nullable;

public interface IModelManager {

    void setTouchEventListener(ITouchEventListener listener);

    boolean onTouchEvent(@Nullable MotionEvent event);
}
