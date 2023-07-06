package com.jin.weather_pager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jin.weather_pager.R;
import com.jin.weather_pager.bean.SVWeatherInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewPagerDateAdapterYlOne extends RecyclerView.Adapter<ViewPagerDateAdapterYlOne.ViewHolder> {



    private Context mContext;
    private List<SVWeatherInfo> mDatas;

    private int selectedIndex = 0;

    private Map<Integer, ViewHolder> maps = new HashMap<>();


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM-dd", Locale.CHINA);


    public ViewPagerDateAdapterYlOne(Context context, List<SVWeatherInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_pager_date_adapter_item_yl_one, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SVWeatherInfo weatherInfo = mDatas.get(position);
        maps.put(position,holder);
        if(!TextUtils.isEmpty(weatherInfo.getDate())) {
            try {
                holder.tvDate.setText(monthDayFormat.format(dateFormat.parse(weatherInfo.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (position == 1) {
            RecyclerView.LayoutParams params =(RecyclerView.LayoutParams) holder.rootView.getLayoutParams();
            params.topMargin = (int) mContext.getResources().getDimension(R.dimen.dp_32);
            holder.rootView.setLayoutParams(params);
            holder.rootView.setRotation(20f);
        }
        if (position == selectedIndex) {
            holder.indicate.setVisibility(View.VISIBLE);
            holder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            holder.rootView.setAlpha(1f);
            holder.tvDate.setTextColor(Color.parseColor("#FF597096"));
        } else {
            holder.indicate.setVisibility(View.GONE);
            holder.rootView.setAlpha(0.6f);
            holder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            holder.tvDate.setTextColor(Color.parseColor("#FFd3d9e3"));
        }

    }


    public Map<Integer,ViewHolder> getMaps() {
        return maps;
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
        public TextView tvDate;

        public View rootView;

        public View indicate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root);
            tvDate = itemView.findViewById(R.id.tv_date);
            indicate = itemView.findViewById(R.id.indicate);
        }
    }
}
