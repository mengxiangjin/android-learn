package com.jin.weather_pager.yl;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.jin.weather_pager.R;
import com.jin.weather_pager.adapter.ViewPagerAdapter;
import com.jin.weather_pager.bean.SVHomeData;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.utils.SVUtils;
import com.jin.weather_pager.yl.adapter.SVWeatherAdapterYlTwo;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SVWeatherFragmentYlTwo extends Fragment {

    ArrayList<SVWeatherInfo> weatherInfos = new ArrayList<>();
    SVWeatherAdapterYlTwo adapter;
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


    RecyclerView rvWeather;
    TextView tvLocateHead;

    ViewPager2 viewPager;
    ViewPagerAdapter pagerAdapter;

    List<SVWeatherInfo> pagerWeatherInfos = new ArrayList<>();

    View circleOne;
    View circleTwo;
    View circleThree;

    private static final float ROTATE = 10f;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_yl_two,null);

        rvWeather = view.findViewById(R.id.rv_weather);
        tvLocateHead = view.findViewById(R.id.tv_locate_head);
        viewPager = view.findViewById(R.id.view_pager);
        circleOne = view.findViewById(R.id.circle_one);
        circleTwo = view.findViewById(R.id.circle_two);
        circleThree = view.findViewById(R.id.circle_three);

        myHandler = new MyHandler(getActivity());
        initView();
        initData();
        return view;
    }


    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWeather.setLayoutManager(linearLayoutManager);
        adapter = new SVWeatherAdapterYlTwo(getActivity(),weatherInfos);
        rvWeather.setAdapter(adapter);

        pagerAdapter = new ViewPagerAdapter(requireContext(), pagerWeatherInfos);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Map<Integer, ViewPagerAdapter.ViewHolder> holderMap = pagerAdapter.getMaps();
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
                    circleOne.setSelected(true);
                    circleTwo.setSelected(false);
                    circleThree.setSelected(false);
                }
                //尾部
                if (nextView == null) {
                    if (preView != null) {
                        startRotateAnim(preView,preView.getRotation(),-ROTATE);
                    }
                    circleOne.setSelected(false);
                    circleTwo.setSelected(false);
                    circleThree.setSelected(true);
                }

                if (preView != null && nextView != null) {
                    startRotateAnim(preView,preView.getRotation(),-ROTATE);
                    startRotateAnim(nextView,nextView.getRotation(),ROTATE);
                    circleOne.setSelected(false);
                    circleTwo.setSelected(true);
                    circleThree.setSelected(false);
                }
                View view = holderMap.get(position).rootView;
                startRotateAnim(view,view.getRotation(),0f);
            }
        });
    }

    private void flushView(SVHomeData homeData){
        if(!TextUtils.isEmpty(homeData.getCity())){
            tvLocateHead.setText(homeData.getCity());
        }
        if(homeData.getList() != null){
            pagerAdapter.setmDatas(homeData.getList(),homeData.getCity());
            circleOne.setSelected(true);
            adapter.setWeatherInfos(homeData.getList());
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
