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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jin.weather_pager.R;
import com.jin.weather_pager.bean.SVHomeData;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.ry.adapter.SVWeatherAdapterRyTwo;
import com.jin.weather_pager.utils.SVUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SVWeatherFragmentRyTwoBlack extends Fragment {
    List<SVWeatherInfo> weatherInfos = new ArrayList<>();
    SVWeatherAdapterRyTwo weatherAdapter;
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
    ImageView imgWeather;
    TextView tvTemperature;

    TextView tvTemperatureMin;

    TextView tvWeather;

    TextView tvCurrentDay;

    TextView tvWindSpeed;

    TextView tvWater;

    TextView tvSun;

    View back;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_ry_two,null);

        rvWeather = view.findViewById(R.id.rv_weather);
        imgWeather = view.findViewById(R.id.img_weather);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvWeather = view.findViewById(R.id.tv_weather);
        tvCurrentDay = view.findViewById(R.id.tv_current_date);
        tvWindSpeed = view.findViewById(R.id.tv_wind_speed);
        tvWater = view.findViewById(R.id.tv_water);
        tvSun = view.findViewById(R.id.tv_sun);
        tvTemperatureMin = view.findViewById(R.id.tv_temperature_min);
        back = view.findViewById(R.id.back);

        myHandler = new MyHandler(getActivity());

        initView();
        initData();
        return view;
    }


    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWeather.setLayoutManager(linearLayoutManager);
        weatherAdapter = new SVWeatherAdapterRyTwo(getActivity(),weatherInfos);
        rvWeather.setAdapter(weatherAdapter);

        back.setOnClickListener((view) -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });
    }


    private void flushView(SVHomeData homeData){

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
                    if (!TextUtils.isEmpty(weatherInfo.getAvg())) {
                        tvTemperature.setText(weatherInfo.getAvg());
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getMin())) {
                        int min = (int) Float.parseFloat(weatherInfo.getMin());
                        tvTemperatureMin.setText("/" + min + "°");
                    }
                }
            }
            weatherAdapter.setWeatherInfos(homeData.getList());
        }

    }


    private void initData(){
        new Thread(() -> {
            SVHomeData homeData = SVUtils.getData();
            Message homeMessage = new Message();
            homeMessage.obj = homeData;
            homeMessage.what = 0x01;
            myHandler.sendMessage(homeMessage);
        }).start();
    }


}
