package com.jin.my_view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jin.my_view.R;

public class InputNumberView extends RelativeLayout {


    private int currentValue = 0;
    private View increaseBtn;
    private EditText valueEdit;
    private View decreaseBtn;

    private int maxValue;
    private int minValue;
    private int step;
    private int defaultValue;
    private boolean disabled;
    private int bgResourceId;

    private OnNumberChangeListener numberChangeListener;

    public InputNumberView(Context context) {
        this(context,null);
    }

    public InputNumberView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public InputNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context,attrs);
        initListener();
    }

    private void initListener() {
        increaseBtn.setOnClickListener((view) -> {
            currentValue += step;
            if (currentValue > maxValue) {
                currentValue = maxValue;
            }
            updateText();
        });
        decreaseBtn.setOnClickListener((view) -> {
            currentValue -= step;
            if (currentValue < minValue) {
                currentValue = minValue;
            }
            updateText();
        });
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.input_number_view, this, false);
        addView(view);
        increaseBtn = view.findViewById(R.id.increase);
        valueEdit = view.findViewById(R.id.value);
        decreaseBtn = view.findViewById(R.id.decrease);
    }
    
    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputNumberView);
        maxValue = typedArray.getInt(R.styleable.InputNumberView_maxValue,10);
        minValue = typedArray.getInt(R.styleable.InputNumberView_minValue,-10);
        step = typedArray.getInt(R.styleable.InputNumberView_step,1);
        defaultValue = typedArray.getInt(R.styleable.InputNumberView_defaultValue,0);
        disabled = typedArray.getBoolean(R.styleable.InputNumberView_disabled,false);
        bgResourceId = typedArray.getResourceId(R.styleable.InputNumberView_btnBg, -1);
        currentValue = defaultValue;
        updateText();
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        updateText();
    }

    public void setNumberChangeListener(OnNumberChangeListener numberChangeListener) {
        this.numberChangeListener = numberChangeListener;
    }

    private void updateText() {
        valueEdit.setText(String.valueOf(currentValue));
        if (numberChangeListener != null) {
            numberChangeListener.onNumberChanged(currentValue);
        }
    }

    public interface OnNumberChangeListener{
        void onNumberChanged(int value);
    }
}
