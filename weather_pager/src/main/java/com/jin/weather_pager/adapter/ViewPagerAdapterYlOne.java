package com.jin.weather_pager.adapter;

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


import java.util.List;

public class ViewPagerAdapterYlOne extends RecyclerView.Adapter<ViewPagerAdapterYlOne.ViewHolder> {



    private Context mContext;
    private List<SVWeatherInfo> mDatas;


    public ViewPagerAdapterYlOne(Context context, List<SVWeatherInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_pager_adapter_item_yl_one, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SVWeatherInfo weatherInfo = mDatas.get(position);
        if (!TextUtils.isEmpty(weatherInfo.getAvg())) {
            holder.tvTemperature.setText(weatherInfo.getAvg()+"℃");
        }
        if (!TextUtils.isEmpty(weatherInfo.getSkyconDesc())) {
            holder.weather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getSkyconDesc()));
        }
        if (!TextUtils.isEmpty(weatherInfo.getDaySkyconDesc())) {
            holder.tvWeather.setText(weatherInfo.getDaySkyconDesc());
        }
        if (!TextUtils.isEmpty(weatherInfo.getHumidityAvg())) {
            holder.tvRainFall.setText(weatherInfo.getHumidityAvg());
        }
        if (!TextUtils.isEmpty(weatherInfo.getUltravioletDesc())) {
            holder.tvSunLevel.setText(weatherInfo.getUltravioletDesc());
        }
        if (!TextUtils.isEmpty(weatherInfo.getMinWindLevel()) && !TextUtils.isEmpty(weatherInfo.getMaxWindLevel())) {
            holder.tvWindLevel.setText(weatherInfo.getMinWindLevel() + "-" + weatherInfo.getMaxWindLevel());
        }

        holder.weatherBg.setImageResource(R.drawable.weather_icon_cloud_bg_yl_one);

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public void setmDatas(List<SVWeatherInfo> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public List<SVWeatherInfo> getmDatas() {
        return mDatas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherBg;

        public ImageView weather;
        public TextView tvTemperature;

        public TextView tvWeather;
        public TextView tvRainFall;
        public TextView tvSunLevel;
        public TextView tvWindLevel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            weatherBg = itemView.findViewById(R.id.img_weather_bg);
            weather = itemView.findViewById(R.id.img_weather);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvWeather = itemView.findViewById(R.id.tv_weather);
            tvRainFall = itemView.findViewById(R.id.tv_rainfall);
            tvSunLevel = itemView.findViewById(R.id.tv_sun_level);
            tvWindLevel = itemView.findViewById(R.id.tv_wind_level);
        }
    }
}
