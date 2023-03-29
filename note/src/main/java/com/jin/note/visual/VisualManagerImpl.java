package com.jin.note.visual;

import android.content.Context;
import android.view.MotionEvent;

import com.jin.note.model.InsertableObject;
import com.jin.note.model.InsertableObjectStroke;

public class VisualManagerImpl implements IVisualManager{


    private Context mContext;

    public VisualManagerImpl(Context context) {
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent, InsertableObject insertableObject) {
        if (insertableObject == null) return false;
        VisualElement visualElement = getVisualElement(insertableObject);
        return visualElement.onTouchEvent(motionEvent);
    }

    private VisualElement getVisualElement(InsertableObject insertableObject) {
        VisualElement visualElement = insertableObject.createVisualElement(mContext);
        visualElement.initVisualElement();
        return visualElement;
    }
}
