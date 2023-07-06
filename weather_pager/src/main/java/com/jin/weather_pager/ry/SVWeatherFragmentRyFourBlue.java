package com.jin.weather_pager.ry;

import android.app.Activity;
import android.graphics.Rect;
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
import com.jin.weather_pager.bean.SVTimeWeatherInfo;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.ry.adapter.SVTimeWeatherAdapterRyFour;
import com.jin.weather_pager.ry.adapter.SVWeatherAdapterRyFour;
import com.jin.weather_pager.utils.SVUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SVWeatherFragmentRyFourBlue extends Fragment {
    List<SVTimeWeatherInfo> timeWeatherInfos = new ArrayList<>();

    List<SVWeatherInfo> weatherInfos = new ArrayList<>();
    SVTimeWeatherAdapterRyFour timeWeatherAdapter;

    SVWeatherAdapterRyFour weatherAdapter;
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

    RecyclerView rvTimeWeather;
    TextView tvCity;
    ImageView imgWeather;

    ImageView forecastWeather;
    TextView tvTemperature;
    TextView tvCurrentDay;
    TextView tvWater;
    TextView tvAtmos;
    TextView tvWindLevel;

    TextView tvForecast;
    TextView tvRemind;


    TextView tvMaxtemperature;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);

    SimpleDateFormat hourFormat = new SimpleDateFormat("HH",Locale.CHINA);
    SimpleDateFormat minuteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);


    private Boolean isNight;

    private String nightSkyconDesc = "";
    private String daySkyconDesc = "";
    private String nextDaySkyconDesc = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout_fragment_home_ry_four,null);

        rvWeather = view.findViewById(R.id.rv_weather);
        rvTimeWeather = view.findViewById(R.id.rv_time_weather);
        tvCity = view.findViewById(R.id.tv_city);
        imgWeather = view.findViewById(R.id.img_weather);
        forecastWeather = view.findViewById(R.id.img_forecast_weather);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvCurrentDay = view.findViewById(R.id.tv_current_date);
        tvWater = view.findViewById(R.id.tv_water);
        tvWindLevel = view.findViewById(R.id.tv_wind_level);
        tvAtmos = view.findViewById(R.id.tv_atmos);
        tvMaxtemperature = view.findViewById(R.id.tv_max_temperature);
        tvForecast = view.findViewById(R.id.tv_forecast);
        tvRemind = view.findViewById(R.id.tv_remind);

        myHandler = new MyHandler(getActivity());

        initView();
        initData();
        return view;
    }


    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rvTimeWeather.setLayoutManager(linearLayoutManager);
        timeWeatherAdapter = new SVTimeWeatherAdapterRyFour(getActivity(),timeWeatherInfos);
        rvTimeWeather.setAdapter(timeWeatherAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvWeather.setLayoutManager(layoutManager);
        weatherAdapter = new SVWeatherAdapterRyFour(getActivity(),weatherInfos);
        rvWeather.setAdapter(weatherAdapter);
        rvWeather.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position != 0) {
                    outRect.top = (int) getResources().getDimension(R.dimen.dp_8);
                }
            }
        });
    }


    private void updateTimeData(List<SVTimeWeatherInfo> timeData) {
        if (!timeData.isEmpty()) {
            SVTimeWeatherInfo currentTimeInfo = timeData.get(0);
            String dateTime = currentTimeInfo.getDateTime();
            try {
                Date parse = minuteFormat.parse(dateTime);
                String currentTime = hourFormat.format(parse);
                //下午
                if (Integer.parseInt(currentTime) > 12) {
                    isNight = true;
                    if (!TextUtils.isEmpty(nightSkyconDesc)) {
                        imgWeather.setImageResource(SVUtils.getWeatherImageResource(nightSkyconDesc));
                    }
                    if (!TextUtils.isEmpty(nextDaySkyconDesc)) {
                        forecastWeather.setImageResource(SVUtils.getWeatherImageResource(nextDaySkyconDesc));
                        tvForecast.setText("明日预计" + nextDaySkyconDesc);
                        if (nextDaySkyconDesc.contains("雨")) {
                            tvRemind.setText("注意带伞！");
                        }
                    }
                } else {
                    isNight = false;
                    if (!TextUtils.isEmpty(nightSkyconDesc)) {
                        forecastWeather.setImageResource(SVUtils.getWeatherImageResource(nightSkyconDesc));
                        tvForecast.setText("晚上预计" + nightSkyconDesc);
                        if (nextDaySkyconDesc.contains("雨")) {
                            tvRemind.setText("注意带伞！");
                        }
                    }
                    if (!TextUtils.isEmpty(daySkyconDesc)) {
                        imgWeather.setImageResource(SVUtils.getWeatherImageResource(daySkyconDesc));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

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
        String today = dateFormat.format(new Date());


        if(homeData.getList() != null){
            for(int i = 0 ;i < homeData.getList().size();i++){
                if(homeData.getList().get(i)!= null && today.equals(homeData.getList().get(i).getDate())){
                    SVWeatherInfo weatherInfo = homeData.getList().get(i);
                    SVWeatherInfo nextDayWeatherInfo = null;
                    if (i != homeData.getList().size() - 1) {
                        nextDayWeatherInfo = homeData.getList().get(i + 1);
                    }
                    
                    if(!TextUtils.isEmpty(weatherInfo.getSkyconDesc())){
                        if (isNight != null) {
                            if (isNight) {
                                if (!TextUtils.isEmpty(weatherInfo.getNightSkyconDesc())) {
                                    imgWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getNightSkyconDesc()));
                                    if (nextDayWeatherInfo != null) {
                                        if (!TextUtils.isEmpty(nextDayWeatherInfo.getDaySkyconDesc())) {
                                            forecastWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getDaySkyconDesc()));
                                            tvForecast.setText("明日预计" + weatherInfo.getDaySkyconDesc());
                                            if (nextDaySkyconDesc.contains("雨")) {
                                                tvRemind.setText("注意带伞！");
                                            }
                                        }
                                    }
                                }

                            } else {
                                if (!TextUtils.isEmpty(weatherInfo.getDaySkycon())) {
                                    imgWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getDaySkycon()));
                                }
                                if (!TextUtils.isEmpty(weatherInfo.getDaySkycon())) {
                                    forecastWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getNightSkyconDesc()));
                                    tvForecast.setText("晚上预计" + weatherInfo.getNightSkyconDesc());
                                    if (nextDaySkyconDesc.contains("雨")) {
                                        tvRemind.setText("注意带伞！");
                                    }
                                }
                            }
                        } else {
                            daySkyconDesc = weatherInfo.getDaySkyconDesc();
                            nightSkyconDesc = weatherInfo.getNightSkyconDesc();
                            if (nextDayWeatherInfo != null) {
                                nextDaySkyconDesc = nextDayWeatherInfo.getDaySkyconDesc();
                            }
                        }
                        imgWeather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getSkyconDesc()));
                    }
                    try {
                        Date date = dateFormat.parse(weatherInfo.getDate());
                        String currentDay = SVUtils.getWeekOfDate(date);
                        String dateStr = monthFormat.format(date);
                        tvCurrentDay.setText(dateStr + " " + currentDay);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(weatherInfo.getHumidityAvg())) {
                        tvWater.setText(weatherInfo.getHumidityAvg() + "%");
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getMinWindLevel())) {
                        tvWindLevel.setText(weatherInfo.getMinWindLevel());
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getAvg())) {
                        tvTemperature.setText(weatherInfo.getAvg()+"°");
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getPressureAvg())) {
                        int pressure = (int) Float.parseFloat(weatherInfo.getPressureAvg());
                        tvAtmos.setText(pressure / 100 + "hPa");
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getMax())) {
                        int maxTemperature = (int) Double.parseDouble(weatherInfo.getMax());
                        tvMaxtemperature.setText("最高温度 " +  maxTemperature + "°");
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

            List<SVTimeWeatherInfo> timeData = SVUtils.getTimeData();
            Message timeMessage = new Message();
            timeMessage.obj = timeData;
            timeMessage.what = 0x02;
            myHandler.sendMessage(timeMessage);
        }).start();
    }

}
