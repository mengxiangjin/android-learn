package com.jin.note.model;


import android.content.Context;

import com.jin.note.visual.VisualElement;

//可插入对象基类
public abstract class InsertableObject {
    public abstract VisualElement createVisualElement(Context mContext);
}
