package com.jin.my_view.loginPage;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jin.my_view.R;

public class LoginPageView extends FrameLayout {

    private int mainColor;
    private int mVerifyCodeLength;

    public LoginPageView(@NonNull Context context) {
        this(context,null);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoginPageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.LoginPageView);
        mainColor = styledAttributes.getColor(R.styleable.LoginPageView_mainColor,0);
        mVerifyCodeLength = styledAttributes.getInt(R.styleable.LoginPageView_verifyCodeLength,0);
    }
}
