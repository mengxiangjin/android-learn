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
import com.jin.weather_pager.bean.SVTimeWeatherInfo;
import com.jin.weather_pager.utils.SVUtils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SVTimeWeatherAdapterRyFour extends RecyclerView.Adapter {

    Context context;
    List<SVTimeWeatherInfo> weatherInfos;

    SimpleDateFormat hourDateFormat = new SimpleDateFormat("HH:mm",Locale.CHINA);
    SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);

    int screenWidth;
    int itemWidth;

    public void setWeatherInfos(List<SVTimeWeatherInfo> weatherInfos) {
        this.weatherInfos = weatherInfos;
        notifyDataSetChanged();
    }

    public SVTimeWeatherAdapterRyFour(Context context, List<SVTimeWeatherInfo> weatherInfos) {
        this.context = context;
        this.weatherInfos = weatherInfos;
        screenWidth = SVUtils.getScreenWidth(context);
        itemWidth = (screenWidth - SVUtils.dip2px(context,24*2))/4;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SVWeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.weather_listitem_time_weather_ry_four,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SVWeatherViewHolder weatherViewHolder = (SVWeatherViewHolder) holder;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) weatherViewHolder.rl_content.getLayoutParams();
        params.width = itemWidth;
        weatherViewHolder.rl_content.setLayoutParams(params);
        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) weatherViewHolder.img_weather.getLayoutParams();
        imageParams.width = itemWidth;
        imageParams.height = itemWidth;
        weatherViewHolder.img_weather.setLayoutParams(imageParams);

        weatherViewHolder.img_weather.setImageResource(SVUtils.getWeatherImageResource(weatherInfos.get(position).getSkyconValue()));

        SVTimeWeatherInfo svTimeWeatherInfo = weatherInfos.get(position);
        if (!TextUtils.isEmpty(svTimeWeatherInfo.getTemperatureValue())) {
            int temperatureValue = (int) Float.parseFloat(svTimeWeatherInfo.getTemperatureValue());
            weatherViewHolder.tv_temperature.setText(temperatureValue + "°");
        }
        if (!TextUtils.isEmpty(svTimeWeatherInfo.getSkyconValue())) {
            weatherViewHolder.img_weather.setImageResource(SVUtils.getWeatherImageResource(svTimeWeatherInfo.getSkyconValue()));
        }

        String time = "";
        if (!TextUtils.isEmpty(svTimeWeatherInfo.getDateTime())) {
            try {
                Date date = timeDateFormat.parse(svTimeWeatherInfo.getDateTime());
                String format = hourDateFormat.format(date);
                int hTime = Integer.parseInt(format.substring(0, 2));
                weatherViewHolder.tv_time.setText(Math.abs(hTime - 12) + "点");
                time = format;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Date date = new Date();
        String currentTime = hourDateFormat.format(date).substring(0,2);
        boolean isCurrentTime = time.contains(currentTime);
        if (isCurrentTime) {
            weatherViewHolder.tv_time.setText("现在");
        }
        weatherViewHolder.tv_bg.setSelected(isCurrentTime);
        weatherViewHolder.tv_time.setSelected(isCurrentTime);
        weatherViewHolder.tv_temperature.setSelected(isCurrentTime);
    }

    @Override
    public int getItemCount() {
        return weatherInfos == null ? 0 : weatherInfos.size();
    }


    private class SVWeatherViewHolder extends RecyclerView.ViewHolder{
        ImageView img_weather;
        TextView tv_temperature;
        ImageView tv_bg;
        RelativeLayout rl_content;
        TextView tv_time;

        public SVWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            img_weather = itemView.findViewById(R.id.img_weather);
            tv_temperature = itemView.findViewById(R.id.tv_temperature);
            tv_bg = itemView.findViewById(R.id.item_bg);
            tv_time = itemView.findViewById(R.id.tv_time);
            rl_content = itemView.findViewById(R.id.rl_content);
        }
    }
}
