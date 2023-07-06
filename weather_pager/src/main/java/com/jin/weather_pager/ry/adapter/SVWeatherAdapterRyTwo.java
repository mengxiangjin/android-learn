package com.jin.weather_pager.ry.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jin.weather_pager.R;
import com.jin.weather_pager.bean.SVWeatherInfo;
import com.jin.weather_pager.utils.SVUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SVWeatherAdapterRyTwo extends RecyclerView.Adapter {

    Context context;
    List<SVWeatherInfo> weatherInfos = null;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM月dd日",Locale.CHINA);

    public void setWeatherInfos(ArrayList<SVWeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
        notifyDataSetChanged();
    }

    public SVWeatherAdapterRyTwo(Context context, List<SVWeatherInfo> weatherInfos) {
        this.context = context;
        this.weatherInfos = weatherInfos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SVWeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.weather_listitem_weather_ry_two,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SVWeatherViewHolder weatherViewHolder = (SVWeatherViewHolder) holder;
        SVWeatherInfo weatherInfo = weatherInfos.get(position);
        if(!TextUtils.isEmpty(weatherInfo.getDate())) {
            String date = weatherInfo.getDate();
            try {
                weatherViewHolder.tv_date.setText(monthDayFormat.format(dateFormat.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(weatherInfo.getSkyconDesc())) {
            String skyconDesc = weatherInfo.getSkyconDesc();
            weatherViewHolder.tv_sky.setText(skyconDesc);
            weatherViewHolder.img_weather.setImageResource(SVUtils.getWeatherImageResource(skyconDesc));
        }
        if (!TextUtils.isEmpty(weatherInfo.getAvg())) {
            weatherViewHolder.tv_temperature.setText("+" + weatherInfo.getAvg() + "°");
        }
        if (!TextUtils.isEmpty(weatherInfo.getMin())) {
            int min = (int) Float.parseFloat(weatherInfo.getMin());
            weatherViewHolder.tv_temperature_min.setText("+" + min + "°");
        }
    }

    @Override
    public int getItemCount() {
        return weatherInfos == null ? 0 : weatherInfos.size();
    }


    private class SVWeatherViewHolder extends RecyclerView.ViewHolder{

        TextView tv_date;
        ImageView img_weather;

        TextView tv_sky;

        TextView tv_temperature;

        TextView tv_temperature_min;




        public SVWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            img_weather = itemView.findViewById(R.id.img_weather);
            tv_temperature = itemView.findViewById(R.id.tv_temperature);
            tv_temperature_min = itemView.findViewById(R.id.tv_temperature_min);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_sky = itemView.findViewById(R.id.tv_sky);
        }
    }
}
