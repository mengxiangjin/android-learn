package com.jin.note.operation;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;

import com.jin.note.model.InsertableObjectStroke;


public class VisualStrokePath extends VisualStroke {

    private Path mPath;
    private float mEndX;
    private float mEndY;



    public VisualStrokePath(Context context, InsertableObjectStroke object) {
        super(context,object);
        mPath = new Path();
    }

    @Override
    public void initVisualElement() {
        mInsertableObjectStroke.setPath(mPath);
    }

    @Override
    public void onDown(MotionElement motionElement) {
        mPath = new Path();
        mEndX = motionElement.x;
        mEndY = motionElement.y;
        Log.d("lzy", "mEndX: " + mEndX);
        Log.d("lzy", "mEndY: " + mEndY);
        mPath.moveTo(motionElement.x,motionElement.y);
    }

    @Override
    public void onMove(MotionElement motionElement) {
        float temEndX = mEndX;
        float temEndY = mEndY;
        mEndX = motionElement.x;
        mEndY = motionElement.y;
        Log.d("lzy", "mEndX1: " + mEndX);
        Log.d("lzy", "mEndY1: " + mEndY);
        mPath.quadTo(temEndX,temEndY,(motionElement.x + temEndX) / 2,(motionElement.y + temEndY) / 2);
    }

    @Override
    public void onUp(MotionElement motionElement) {
        mEndX = motionElement.x;
        mEndY = motionElement.y;
        mPath.lineTo(mEndX,mEndY);
    }

    @Override
    public void onCancel(MotionElement motionElement) {

    }
}
