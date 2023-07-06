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
import com.jin.weather_pager.ry.adapter.SVWeatherAdapterRyThree;
import com.jin.weather_pager.utils.SVUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SVWeatherFragmentRyThreeWhite extends Fragment {
    ArrayList<SVWeatherInfo> weatherInfos = new ArrayList<>();
    SVWeatherAdapterRyThree adapter;
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
    TextView tvCity;
    ImageView imgWeather;
    TextView tvTemperature;

    TextView tvWeather;

    TextView tvCurrentDay;

    TextView tvPmTwo;
    TextView tvPmTen;
    TextView tvSOTwo;
    TextView tvNOTwo;
    TextView tvCO;
    TextView tvOThree;

    ImageView refresh;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_ry_three,null);

        rvWeather = view.findViewById(R.id.rv_weather);
        tvCity = view.findViewById(R.id.tv_city);
        imgWeather = view.findViewById(R.id.img_weather);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvWeather = view.findViewById(R.id.tv_weather);
        tvCurrentDay = view.findViewById(R.id.tv_current_date);
        refresh = view.findViewById(R.id.img_refresh);

        tvPmTwo = view.findViewById(R.id.tv_pm25);
        tvPmTen = view.findViewById(R.id.tv_pm10);
        tvSOTwo = view.findViewById(R.id.tv_so2);
        tvNOTwo = view.findViewById(R.id.tv_no2);
        tvCO = view.findViewById(R.id.tv_co);
        tvOThree = view.findViewById(R.id.tv_o3);


        myHandler = new MyHandler(getActivity());

        initView();
        initData();
        return view;
    }


    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWeather.setLayoutManager(linearLayoutManager);
        adapter = new SVWeatherAdapterRyThree(getActivity(),weatherInfos);
        rvWeather.setAdapter(adapter);

        refresh.setOnClickListener((view) -> {
            initData();
        });
    }

    private void flushView(SVHomeData homeData){
        if(!TextUtils.isEmpty(homeData.getCity())){
            tvCity.setText(homeData.getCity());
        }
        if(!TextUtils.isEmpty(homeData.getDistrict())){
            String city = tvCity.getText().toString();
            tvCity.setText(city + "," +homeData.getDistrict());
        }
        if(!TextUtils.isEmpty(homeData.getAirQualityPm25())){
            tvPmTwo.setText(homeData.getAirQualityPm25());
        }
        if(!TextUtils.isEmpty(homeData.getAirQualityPm10())){
            tvPmTen.setText(homeData.getAirQualityPm10());
        }
        if(!TextUtils.isEmpty(homeData.getAirQualitySo2())){
            tvSOTwo.setText(homeData.getAirQualitySo2());
        }
        if(!TextUtils.isEmpty(homeData.getAirQualityCo())){
            tvCO.setText(homeData.getAirQualityCo());
        }
        if(!TextUtils.isEmpty(homeData.getAirQualityNo2())){
            tvNOTwo.setText(homeData.getAirQualityNo2());
        }
        if(!TextUtils.isEmpty(homeData.getAirQuality03())){
            tvOThree.setText(homeData.getAirQuality03());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/dd", Locale.CHINA);
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
                        tvCurrentDay.setText(currentDay + ", " + dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    tvTemperature.setText(weatherInfo.getAvg()+"°");
                }
            }

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


}
