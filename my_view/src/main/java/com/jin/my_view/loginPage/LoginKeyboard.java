package com.jin.my_view.loginPage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jin.my_view.R;

public class LoginKeyboard extends LinearLayout implements View.OnClickListener {


    private OnNumChangedListener numChangedListener;

    public LoginKeyboard(Context context) {
        this(context,null);
    }

    public LoginKeyboard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoginKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View root = LayoutInflater.from(context).inflate(R.layout.num_key_pad, this, false);
        addView(root);

        root.findViewById(R.id.number_0).setOnClickListener(this);
        root.findViewById(R.id.number_1).setOnClickListener(this);
        root.findViewById(R.id.number_2).setOnClickListener(this);
        root.findViewById(R.id.number_3).setOnClickListener(this);
        root.findViewById(R.id.number_4).setOnClickListener(this);
        root.findViewById(R.id.number_5).setOnClickListener(this);
        root.findViewById(R.id.number_6).setOnClickListener(this);
        root.findViewById(R.id.number_7).setOnClickListener(this);
        root.findViewById(R.id.number_8).setOnClickListener(this);
        root.findViewById(R.id.number_9).setOnClickListener(this);
        root.findViewById(R.id.del).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (numChangedListener == null) {
            Log.d("lzy","invalid listener");
            return;
        }
        if (v.getId() == R.id.del) {
            numChangedListener.onDelPress();
        } else  {
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                String num = textView.getText().toString();
                numChangedListener.onNumPress(Integer.parseInt(num));
            }
        }
    }

    public void setNumChangedListener(OnNumChangedListener listener) {
        numChangedListener = listener;
    }

    public interface OnNumChangedListener {
        void onNumPress(int num);
        void onDelPress();
    }
}
