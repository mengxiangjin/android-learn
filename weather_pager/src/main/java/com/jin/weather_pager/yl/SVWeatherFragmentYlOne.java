package com.jin.weather_pager.yl;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.jin.weather_pager.R;
import com.jin.weather_pager.adapter.ViewPagerAdapterYlOne;
import com.jin.weather_pager.adapter.ViewPagerDateAdapterYlOne;
import com.jin.weather_pager.bean.SVHomeData;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.utils.SVUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SVWeatherFragmentYlOne extends Fragment {

    MyHandler myHandler;

    private class MyHandler extends Handler{
        WeakReference<Activity> weakReference ;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SVHomeData homeData = (SVHomeData) msg.obj;
            if(homeData != null){
                flushView(homeData);
            }
        }
    }

    TextView tvLocateHead;

    ViewPager2 viewPager;

    ViewPager2 dateViewPager;
    ViewPagerAdapterYlOne pagerAdapter;

    ViewPagerDateAdapterYlOne dateAdapter;
    List<SVWeatherInfo> pagerWeatherInfos = new ArrayList<>();

    List<SVWeatherInfo> datePagerWeatherInfos = new ArrayList<>();

    private static final float ROTATE = 10f;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_yl_one,null);
        tvLocateHead = view.findViewById(R.id.tv_locate_head);
        viewPager = view.findViewById(R.id.view_pager);
        dateViewPager = view.findViewById(R.id.view_pager_date);

        myHandler = new MyHandler(getActivity());
        initView();
        initData();
        return view;
    }


    private void initView(){
        pagerAdapter = new ViewPagerAdapterYlOne(requireContext(), pagerWeatherInfos);
        dateAdapter = new ViewPagerDateAdapterYlOne(requireContext(),datePagerWeatherInfos);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        dateViewPager.setAdapter(dateAdapter);
        dateViewPager.setOffscreenPageLimit(3);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                dateViewPager.setCurrentItem(position);
            }
        });
        dateViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Map<Integer, ViewPagerDateAdapterYlOne.ViewHolder> holderMap = dateAdapter.getMaps();
                int preIndex = position - 1;
                int nextIndex = position + 1;
                View preView = null;
                if (preIndex != -1) {
                    preView = holderMap.get(preIndex).rootView;
                }
                View nextView = null;
                if (nextIndex != pagerAdapter.getmDatas().size()) {
                    nextView = holderMap.get(nextIndex).rootView;
                }
                //首部
                if (preView == null) {
                    if (nextView != null) {
                        startRotateAnim(nextView,nextView.getRotation(),ROTATE);
                    }
                }
                //尾部
                if (nextView == null) {
                    if (preView != null) {
                        startRotateAnim(preView,preView.getRotation(),-ROTATE);
                    }
                }

                if (preView != null && nextView != null) {
                    startRotateAnim(preView,preView.getRotation(),-ROTATE);
                    startRotateAnim(nextView,nextView.getRotation(),ROTATE);

                    RecyclerView.LayoutParams preParams =(RecyclerView.LayoutParams) preView.getLayoutParams();
                    preParams.topMargin = (int) requireContext().getResources().getDimension(R.dimen.dp_32);
                    preView.setLayoutParams(preParams);

                    RecyclerView.LayoutParams nextParams =(RecyclerView.LayoutParams) nextView.getLayoutParams();
                    nextParams.topMargin = (int) requireContext().getResources().getDimension(R.dimen.dp_32);
                    nextView.setLayoutParams(nextParams);

                    ViewPagerDateAdapterYlOne.ViewHolder preViewHolder = holderMap.get(preIndex);
                    ViewPagerDateAdapterYlOne.ViewHolder nextViewHolder = holderMap.get(nextIndex);
                    preViewHolder.indicate.setVisibility(View.GONE);
                    preViewHolder.rootView.setAlpha(0.6f);
                    preViewHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    preViewHolder.tvDate.setTextColor(Color.parseColor("#FFd3d9e3"));

                    nextViewHolder.indicate.setVisibility(View.GONE);
                    nextViewHolder.rootView.setAlpha(0.6f);
                    nextViewHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    nextViewHolder.tvDate.setTextColor(Color.parseColor("#FFd3d9e3"));
                }

                if (nextView == null && preView != null) {
                    RecyclerView.LayoutParams preParams =(RecyclerView.LayoutParams) preView.getLayoutParams();
                    preParams.topMargin = (int) requireContext().getResources().getDimension(R.dimen.dp_32);
                    preView.setLayoutParams(preParams);

                    ViewPagerDateAdapterYlOne.ViewHolder preViewHolder = holderMap.get(preIndex);
                    preViewHolder.indicate.setVisibility(View.GONE);
                    preViewHolder.rootView.setAlpha(0.6f);
                    preViewHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    preViewHolder.tvDate.setTextColor(Color.parseColor("#FFd3d9e3"));
                }
                if (preView == null && nextView != null) {

                    RecyclerView.LayoutParams nextParams =(RecyclerView.LayoutParams) nextView.getLayoutParams();
                    nextParams.topMargin = (int) requireContext().getResources().getDimension(R.dimen.dp_32);
                    nextView.setLayoutParams(nextParams);

                    ViewPagerDateAdapterYlOne.ViewHolder nextViewHolder = holderMap.get(nextIndex);
                    nextViewHolder.indicate.setVisibility(View.GONE);
                    nextViewHolder.rootView.setAlpha(0.6f);
                    nextViewHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    nextViewHolder.tvDate.setTextColor(Color.parseColor("#FFd3d9e3"));
                }
                ViewPagerDateAdapterYlOne.ViewHolder viewHolder = holderMap.get(position);
                View view = viewHolder.rootView;
                RecyclerView.LayoutParams params =(RecyclerView.LayoutParams) view.getLayoutParams();
                params.topMargin = (int) requireContext().getResources().getDimension(R.dimen.dp_0);
                view.setLayoutParams(params);

                viewHolder.indicate.setVisibility(View.VISIBLE);
                viewHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                viewHolder.rootView.setAlpha(1f);
                viewHolder.tvDate.setTextColor(Color.parseColor("#FF597096"));

                startRotateAnim(view,view.getRotation(),0f);
                viewPager.setCurrentItem(position);
            }
        });
    }

    private void flushView(SVHomeData homeData){
        if(!TextUtils.isEmpty(homeData.getCity())){
            tvLocateHead.setText(homeData.getCity());
        }
        if(homeData.getList() != null){
            pagerAdapter.setmDatas(homeData.getList());
            dateAdapter.setmDatas(homeData.getList());
        }
    }


    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SVHomeData homeData = SVUtils.getData();
                Message message = new Message();
                message.obj = homeData;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    private void startRotateAnim(View target,Float fromRotate,Float toRotate) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", fromRotate, toRotate);
        animator.setDuration(100);
        animator.start();
    }


}
