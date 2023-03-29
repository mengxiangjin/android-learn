package com.jin.note.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;

import com.jin.note.operation.VisualStrokePath;
import com.jin.note.untils.StrokeType;
import com.jin.note.visual.VisualElement;

public class InsertableObjectStroke extends InsertableObject{

    private int mStrokeType;
    private int mColor;
    private int mWidth;
    private int mAlpha;

    private Path mPath;

    public InsertableObjectStroke(int type) {
        super();
        mStrokeType = type;
        mPath = new Path();
        mAlpha = 255;
        mColor = Color.RED;
        mWidth = 30;
    }


    public static InsertableObjectStroke create(int strokeType) {
        InsertableObjectStroke stroke = new InsertableObjectStroke(strokeType);
        return stroke;
    }


    @Override
    public VisualElement createVisualElement(Context mContext) {
        switch (mStrokeType) {
            case StrokeType.NORMAL: {
                return new VisualStrokePath(mContext,this);
            }
            default: {
                return null;
            }
        }
    }


    public void setPath(Path path) {
        mPath.reset();
        mPath.set(path);
    }
}
