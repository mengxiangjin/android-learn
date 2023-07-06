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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SVWeatherAdapterRyFour extends RecyclerView.Adapter {

    Context context;
    List<SVWeatherInfo> weatherInfos = null;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM月dd日",Locale.CHINA);

    public void setWeatherInfos(ArrayList<SVWeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
        notifyDataSetChanged();
    }

    public SVWeatherAdapterRyFour(Context context, List<SVWeatherInfo> weatherInfos) {
        this.context = context;
        this.weatherInfos = weatherInfos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View rootView = layoutInflater.inflate(R.layout.weather_listitem_weather_ry_four, parent, false);
        return new SVWeatherViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SVWeatherViewHolder weatherViewHolder = (SVWeatherViewHolder) holder;

        SVWeatherInfo weatherInfo = weatherInfos.get(position);
        if(!TextUtils.isEmpty(weatherInfo.getDate())) {
            try {
                String date = weatherInfo.getDate();
                Date parseDate = dateFormat.parse(date);
                String calendar = monthDayFormat.format(parseDate);
                String weekOfDate = SVUtils.getWeekOfDate(parseDate);
                weatherViewHolder.tv_date.setText(calendar + " " + weekOfDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(weatherInfo.getMax())) {
            int max = (int) Float.parseFloat(weatherInfo.getMax());
            weatherViewHolder.tv_max_temperature.setText(max + "°");
        }
        if (!TextUtils.isEmpty(weatherInfo.getMin())) {
            int min = (int) Float.parseFloat(weatherInfo.getMin());
            weatherViewHolder.tv_min_temperature.setText("/" + min + "°");
        }
        if (!TextUtils.isEmpty(weatherInfo.getSkyconDesc())) {
            weatherViewHolder.img_weather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getSkyconDesc()));
            weatherViewHolder.tv_sky.setText(weatherInfo.getSkyconDesc());
        }

    }

    @Override
    public int getItemCount() {
        return weatherInfos == null ? 0 : weatherInfos.size();
    }


    private class SVWeatherViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date;
        TextView tv_max_temperature;
        TextView tv_min_temperature;
        ImageView img_weather;
        TextView tv_sky;

        public SVWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_max_temperature = itemView.findViewById(R.id.tv_max_temperature);
            tv_min_temperature = itemView.findViewById(R.id.tv_min_temperature);
            img_weather = itemView.findViewById(R.id.img_weather);
            tv_sky = itemView.findViewById(R.id.tv_sky);
        }
    }
}
