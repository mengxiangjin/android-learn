package com.jin.weather_pager.ry;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jin.weather_pager.R;
import com.jin.weather_pager.bean.SVHomeData;
import com.jin.weather_pager.bean.SVTimeWeatherInfo;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.ry.adapter.SVTimeWeatherAdapterRyOne;
import com.jin.weather_pager.utils.SVUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SVWeatherFragmentRyOneBlack extends Fragment {
    List<SVTimeWeatherInfo> weatherInfos = new ArrayList<>();
    SVTimeWeatherAdapterRyOne timeWeatherAdapter;
    MyHandler myHandler;
    private class MyHandler extends Handler{
        WeakReference<Activity> weakReference ;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    SVHomeData homeData = (SVHomeData) msg.obj;
                    if(homeData != null){
                        flushView(homeData);
                    }
                    break;
                case 0x02:
                    List<SVTimeWeatherInfo> timeData = (List<SVTimeWeatherInfo>)msg.obj;
                    if(timeData != null){
                        updateTimeData(timeData);
                    }
                    break;
            }
        }


    }


    RecyclerView rvWeather;
    TextView tvCity;
    ImageView imgWeather;
    TextView tvTemperature;

    TextView tvWeather;

    TextView tvCurrentDay;

    TextView tvWindSpeed;

    TextView tvWater;

    TextView tvSun;

    ConstraintLayout clDayWeather;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_ry_one,null);

        rvWeather = view.findViewById(R.id.rv_weather);
        tvCity = view.findViewById(R.id.tv_city);
        imgWeather = view.findViewById(R.id.img_weather);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvWeather = view.findViewById(R.id.tv_weather);
        tvCurrentDay = view.findViewById(R.id.tv_current_date);
        tvWindSpeed = view.findViewById(R.id.tv_wind_speed);
        tvWater = view.findViewById(R.id.tv_water);
        tvSun = view.findViewById(R.id.tv_sun);
        clDayWeather = view.findViewById(R.id.cl_day_weather);

        myHandler = new MyHandler(getActivity());

        initView();
        initData();
        return view;
    }


    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWeather.setLayoutManager(linearLayoutManager);
        timeWeatherAdapter = new SVTimeWeatherAdapterRyOne(getActivity(),weatherInfos);
        rvWeather.setAdapter(timeWeatherAdapter);

        clDayWeather.setOnClickListener((view) ->{
            if (getFragmentManager() != null) {
                SVWeatherFragmentRyTwoBlack svWeatherFragmentRyTwoBlack = new SVWeatherFragmentRyTwoBlack();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, svWeatherFragmentRyTwoBlack,null).addToBackStack(null).commit();
            }
        });
    }


    private void updateTimeData(List<SVTimeWeatherInfo> timeData) {
        timeWeatherAdapter.setWeatherInfos(timeData);
    }

    private void flushView(SVHomeData homeData){
        if(!TextUtils.isEmpty(homeData.getCity())){
            tvCity.setText(homeData.getCity());
        }
        if(!TextUtils.isEmpty(homeData.getDistrict())){
            String city = tvCity.getText().toString();
            tvCity.setText(city + "," +homeData.getDistrict());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        String today = dateFormat.format(new Date());

        if(homeData.getList() != null){
            for(int i = 0 ;i < homeData.getList().size();i++){
                if(homeData.getList().get(i)!= null && today.equals(homeData.getList().get(i).getDate())){
                    SVWeatherInfo weatherInfo = homeData.getList().get(i);
                    if(!TextUtils.isEmpty(weatherInfo.getSkyconDesc())){
                        imgWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getSkyconDesc()));
                        tvWeather.setText(weatherInfo.getSkyconDesc());
                    }
                    try {
                        Date date = dateFormat.parse(weatherInfo.getDate());
                        String currentDay = SVUtils.getWeekOfDate(date);
                        String dateStr = monthFormat.format(date);
                        tvCurrentDay.setText(dateStr + " " + currentDay);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(weatherInfo.getMaxWindSpeed())) {
                        tvWindSpeed.setText(weatherInfo.getMaxWindSpeed() + " km/h");
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getHumidityAvg())) {
                        tvWater.setText(weatherInfo.getHumidityAvg() + "%");
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getUltravioletIndex())) {
                        tvSun.setText(weatherInfo.getUltravioletIndex() + "");
                    }
                    tvTemperature.setText(weatherInfo.getAvg()+"℃");
                }
            }
        }


    }


    private void initData(){
        new Thread(() -> {
            SVHomeData homeData = SVUtils.getData();
            Message homeMessage = new Message();
            homeMessage.obj = homeData;
            homeMessage.what = 0x01;
            myHandler.sendMessage(homeMessage);

            List<SVTimeWeatherInfo> timeData = SVUtils.getTimeData();
            Message timeMessage = new Message();
            timeMessage.obj = timeData;
            timeMessage.what = 0x02;
            myHandler.sendMessage(timeMessage);
        }).start();
    }


}
