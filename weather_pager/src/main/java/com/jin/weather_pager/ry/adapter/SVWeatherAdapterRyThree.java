package com.jin.weather_pager.ry.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jin.weather_pager.R;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.utils.SVUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SVWeatherAdapterRyThree extends RecyclerView.Adapter {

    Context context;
    ArrayList<SVWeatherInfo> weatherInfos = null;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM/dd",Locale.CHINA);

    int screenWidth = 0;
    int itemWidth = 0;

    public void setWeatherInfos(ArrayList<SVWeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
        notifyDataSetChanged();
    }

    public SVWeatherAdapterRyThree(Context context, ArrayList<SVWeatherInfo> weatherInfos) {
        this.context = context;
        this.weatherInfos = weatherInfos;
        screenWidth = SVUtils.getScreenWidth(context);
        itemWidth = (screenWidth - SVUtils.dip2px(context,24*2))/4;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SVWeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.weather_listitem_weather_ry_three,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SVWeatherViewHolder weatherViewHolder = (SVWeatherViewHolder) holder;
        String currentDay = SVUtils.getDayText(weatherInfos.get(position).getDate());
        weatherViewHolder.tv_currentDay.setText(currentDay+"");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weatherViewHolder.rl_content.getLayoutParams();
        params.width = itemWidth;
        weatherViewHolder.rl_content.setLayoutParams(params);
        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) weatherViewHolder.img_weather.getLayoutParams();
        imageParams.width = itemWidth;
        imageParams.height = itemWidth;
        weatherViewHolder.img_weather.setLayoutParams(imageParams);


        if(!TextUtils.isEmpty(weatherInfos.get(position).getDate())) {
            try {
                weatherViewHolder.tv_date.setText(monthDayFormat.format(dateFormat.parse(weatherInfos.get(position).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!TextUtils.isEmpty(weatherInfos.get(position).getSkyconDesc())){
            weatherViewHolder.tv_sky.setText(weatherInfos.get(position).getSkyconDesc());

        }
        weatherViewHolder.img_weather.setImageResource(SVUtils.getWeatherImageResource(weatherInfos.get(position).getSkyconDesc()));

        weatherViewHolder.tv_temperature.setText(weatherInfos.get(position).getAvg()+"℃");

        boolean isToday = "今天".equals(currentDay);
        weatherViewHolder.tv_date.setSelected(isToday);
        if(isToday){
            weatherViewHolder.tv_bg.setVisibility(View.VISIBLE);
        } else {
            weatherViewHolder.tv_bg.setVisibility(View.GONE);
        }
        weatherViewHolder.tv_currentDay.setSelected(isToday);
        weatherViewHolder.tv_sky.setSelected(isToday);
        weatherViewHolder.tv_temperature.setSelected(isToday);
    }

    @Override
    public int getItemCount() {
        return weatherInfos == null ? 0 : weatherInfos.size();
    }


    private class SVWeatherViewHolder extends RecyclerView.ViewHolder{
        TextView tv_currentDay;
        ImageView img_weather;
        TextView tv_temperature;
        RelativeLayout rl_content;
        TextView tv_date;

        ImageView tv_bg;

        TextView tv_sky;



        public SVWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_currentDay = itemView.findViewById(R.id.tv_currentDay);
            img_weather = itemView.findViewById(R.id.img_weather);
            tv_temperature = itemView.findViewById(R.id.tv_temperature);
            rl_content = itemView.findViewById(R.id.rl_content);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_bg = itemView.findViewById(R.id.item_bg);
            tv_sky = itemView.findViewById(R.id.tv_sky);
        }
    }
}
