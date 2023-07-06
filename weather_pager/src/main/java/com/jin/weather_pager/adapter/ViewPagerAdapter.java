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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {



    private Context mContext;
    private List<SVWeatherInfo> mDatas;

    private String locate;

    private Map<Integer,ViewHolder> maps = new HashMap<>();


    public ViewPagerAdapter(Context context, List<SVWeatherInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_pager_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SVWeatherInfo weatherInfo = mDatas.get(position);
        maps.put(position,holder);
        holder.tvLocate.setText(locate);
        if (!TextUtils.isEmpty(weatherInfo.getAvg())) {
            holder.tvTemperature.setText(weatherInfo.getAvg()+"℃");
        }
        if (!TextUtils.isEmpty(weatherInfo.getSkyconDesc())) {
            holder.weather.setImageResource(SVUtils.getWeatherImageResource(weatherInfo.getSkyconDesc()));
        }
        if (!TextUtils.isEmpty(weatherInfo.getDaySkyconDesc())) {
            holder.tvWeather.setText(weatherInfo.getDaySkyconDesc());
        }
        holder.weatherBg.setImageResource(R.drawable.weather_icon_bg_thunder_yl_two);
        if (position == 1) {
            holder.rootView.setRotation(20f);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public Map<Integer, ViewHolder> getMaps() {
        return maps;
    }

    public void setmDatas(List<SVWeatherInfo> mDatas, String locate) {
        this.mDatas = mDatas;
        this.locate = locate;
        notifyDataSetChanged();
    }

    public List<SVWeatherInfo> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<SVWeatherInfo> mDatas) {
        this.mDatas = mDatas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherBg;

        public ImageView weather;
        public TextView tvLocate;
        public TextView tvTemperature;

        public TextView tvWeather;

        public View rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.root);
            weatherBg = itemView.findViewById(R.id.img_weather_bg);
            tvLocate = itemView.findViewById(R.id.tv_locate);
            weather = itemView.findViewById(R.id.img_weather);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvWeather = itemView.findViewById(R.id.tv_weather);
        }
    }
}
