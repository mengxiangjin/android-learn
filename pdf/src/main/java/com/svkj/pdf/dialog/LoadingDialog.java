package com.svkj.pdf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.svkj.pdf.R;


/**
 * Created by qi.yang on 2016/5/4.
 */
public class LoadingDialog {

    Context mContext;
    Dialog mDia;
    View mView;
    TipListener listener;

    String content;
//    long time = 10000;//默认10s后关闭

    public void setTipListener(TipListener tipListener) {
        this.listener = tipListener;
    }


    public LoadingDialog(Context context, String content) {
        this.content = content;
        mContext = context;
        Init();
    }

    public LoadingDialog(Context context) {
        this.content = content;
        mContext = context;
        Init();
    }




    private void Init() {
        mDia = new Dialog(mContext, R.style.dialog);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        Window win = mDia.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);


        TextView tv_content = mView.findViewById(R.id.tv_content);


        if(!TextUtils.isEmpty(content)){
            tv_content.setText(content);
        }
//
//        if(time != 0){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(mDia!=null && mDia.isShowing()){
//                        mDia.dismiss();
//                    }
//                }
//            },time);
//        }

        ImageView img_loading = mView.findViewById(R.id.img_loading);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
        animation.setInterpolator(new LinearInterpolator());
        img_loading.startAnimation(animation);

        mDia.show();
        mDia.setContentView(mView);
        mDia.setCancelable(false);
        mDia.setCanceledOnTouchOutside(false);

    }

    public void show() {
        mDia.show();
    }

    public void dismiss() {
        if(mDia != null) {
            mDia.dismiss();
        }
    }

    public interface TipListener {
        void clickSure();
        void clickCancel();
    }



}
