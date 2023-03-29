package com.jin.note.manager;

import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.jin.note.doodle.ITouchEventListener;
import com.jin.note.model.InsertableObject;
import com.jin.note.model.InsertableObjectStroke;
import com.jin.note.untils.StrokeType;

import java.util.ArrayList;
import java.util.List;

public class ModelManager implements IModelManager{


    private Context mContext;
    private ITouchEventListener touchEventListener;
    private final List<InsertableObject> mActingInsertableObjects = new ArrayList<InsertableObject>();

    public ModelManager(Context context) {
        mContext = context;
    }
    @Override
    public void setTouchEventListener(ITouchEventListener listener) {
        this.touchEventListener = listener;
    }

    @Override
    public boolean onTouchEvent(@Nullable MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                int strokeType = StrokeType.NORMAL;
                InsertableObjectStroke stroke = InsertableObjectStroke.create(strokeType);
                mActingInsertableObjects.clear();
                mActingInsertableObjects.add(stroke);
                break;
            default:
                break;
        }
        InsertableObject activeObject = getActingInsertableObject();
        if (touchEventListener != null) {
            for (InsertableObject object : mActingInsertableObjects) {
                if (touchEventListener.onTouchEvent(event,object)) {
                    break;
                }
            }
        }
        return true;
    }


    private InsertableObject getActingInsertableObject() {
        if (mActingInsertableObjects.isEmpty()) return null;
        return mActingInsertableObjects.get(0);
    }
}
