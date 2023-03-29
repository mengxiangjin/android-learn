package com.jin.note.doodle;

import android.view.MotionEvent;

import com.jin.note.model.InsertableObject;

public interface ITouchEventListener {


    boolean onTouchEvent(MotionEvent motionEvent, InsertableObject insertableObject);
}
