package com.jin.note.operation;

import android.content.Context;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.jin.note.model.InsertableObjectStroke;
import com.jin.note.visual.VisualElement;

public abstract class VisualStroke extends VisualElement {

    protected Paint mPaint;

    protected InsertableObjectStroke mInsertableObjectStroke;


    public VisualStroke(Context context,InsertableObjectStroke objectStroke) {
        this.mInsertableObjectStroke = objectStroke;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(MotionElement.create(motionEvent));
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(MotionElement.create(motionEvent));
                return true;
            case MotionEvent.ACTION_UP:
                onUp(MotionElement.create(motionEvent));
                return true;
            case MotionEvent.ACTION_CANCEL:
                onCancel(MotionElement.create(motionEvent));
                return true;
            default:
                break;
        }
        return false;
    }


    public abstract void onDown(MotionElement motionElement);
    public abstract void onMove(MotionElement motionElement);
    public abstract void onUp(MotionElement motionElement);
    public abstract void onCancel(MotionElement motionElement);



    public static final class MotionElement {
        /**
         * @see MotionEvent#getX(int)
         */
        public final float x;
        /**
         * @see MotionEvent#getX(int)
         */
        public final float y;
        /**
         * @see MotionEvent#getRawX(int)
         */
        public final float rawX;
        /**
         * @see MotionEvent#getRawY(int)
         */
        public final float rawY;
        /**
         * @see MotionEvent#getPressure()
         */
        public final float pressure;
        /**
         * @see MotionEvent#getToolType(int)
         */
        public final int toolType;
        /**
         * @see MotionEvent#getEventTime()
         */
        public final long timestamp;

        public MotionElement(float mx, float my, float mp, int ttype, long mt) {
            this(mx, my, 0F, 0F, mp, ttype, mt);
        }

        public MotionElement(float mx, float my, float mRawX, float mRawY, float mp, int ttype, long mt) {
            x = mx;
            y = my;
            rawX = mRawX;
            rawY = mRawY;
            pressure = mp;
            toolType = ttype;
            timestamp = mt;
        }

        public static MotionElement create(MotionEvent event, int pointerIndex) {
            return new MotionElement(event.getX(pointerIndex),
                    event.getY(pointerIndex),
                    event.getRawX(),
                    event.getRawY(),
                    event.getPressure(),
                    event.getToolType(pointerIndex),
                    event.getEventTime());
        }

        public static MotionElement create(MotionEvent event) {
            return create(event, 0);
        }
    }
}
