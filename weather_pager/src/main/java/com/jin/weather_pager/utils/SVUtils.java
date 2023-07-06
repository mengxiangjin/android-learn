package com.jin.weather_pager.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.jin.weather_pager.bean.SVHomeData;
import com.jin.weather_pager.bean.SVJsonParser;
import com.jin.weather_pager.bean.SVTimeWeatherInfo;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SVUtils {
    public static String TAG = "SVWeather";

    public static int getWeatherImageResource(String weatherName) {
        for (int i = 0; i < SVConstantsPool.WEATHER_NAMES.length; i++) {
            if (SVConstantsPool.WEATHER_NAMES[i].equals(weatherName)) {
                return SVConstantsPool.WEATHER_IMAGES[i];
            }
        }
        return SVConstantsPool.WEATHER_IMAGES[SVConstantsPool.WEATHER_IMAGES.length - 1];
    }


    public static String getDayText(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(calendar.DAY_OF_YEAR);

        try {
            calendar.setTime(simpleDateFormat.parse(date));
            calendar.add(Calendar.DAY_OF_YEAR,1);
            if(day == calendar.get(Calendar.DAY_OF_YEAR)){
                return "昨天";
            }
            calendar.add(Calendar.DAY_OF_YEAR,-1);
            if(day == calendar.get(Calendar.DAY_OF_YEAR)){
                return "今天";
            }
            calendar.add(Calendar.DAY_OF_YEAR,-1);
            if(day == calendar.get(Calendar.DAY_OF_YEAR)){
                return "明天";
            }
            calendar.add(Calendar.DAY_OF_YEAR,1);
            return getWeekOfDate(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
            return getWeekOfDate(new Date());
        }

    }

    /**
     * 获取今天是周几
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取今天是星期几
     * @return
     */
    public static String getWeekOfDateXQ(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }



    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2Px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static String getWelcomeTip(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=0 && hour <6){
            return "凌晨好！";
        }else if(hour >=6 && hour < 12){
            return "上午好！";
        }else if(hour == 12){
            return "中午好！";
        }else if(hour > 12 && hour <= 18){
            return "下午好！";
        }else{
            return "晚上好！";
        }
    }

    public static SVHomeData getData() {
        SVHomeData svHomeData = null;
        try {
            String baseUrl = "http://82.157.113.125:3006/api/tianqi/caiyun/sdkWeather";
            HashMap<String,Object> paramsMap = new HashMap<>();
            //合成参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key,  URLEncoder.encode((String) paramsMap.get(key),"utf-8")));
                pos++;
            }
            String params =tempParams.toString();
            // 请求的参数转换为byte数组
            byte[] postData = params.getBytes();
            // 新建一个URL对象
            URL url = new URL(baseUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 设置请求头中的内容 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json");

            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write(postData);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String result = streamToString(urlConn.getInputStream());
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject != null && jsonObject.has("content")) {
                    svHomeData = SVJsonParser.fromJson(jsonObject.getString("content"), SVHomeData.class);
                }

                Log.e(TAG, "Post方式请求成功，result--->" + result);
            } else {
                Log.e(TAG, "Post方式请求失败"+urlConn.getResponseCode());
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return svHomeData;
    }


    public static List<SVTimeWeatherInfo> getTimeData() {
        List<SVTimeWeatherInfo> datas = new ArrayList<>();
        try {
            String baseUrl = "http://82.157.113.125:3006/api/tianqi/caiyun/manyHours";
            HashMap<String,Object> paramsMap = new HashMap<>();
            //合成参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key,  URLEncoder.encode((String) paramsMap.get(key),"utf-8")));
                pos++;
            }
            String params =tempParams.toString();
            // 请求的参数转换为byte数组
            byte[] postData = params.getBytes();
            // 新建一个URL对象
            URL url = new URL(baseUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 设置请求头中的内容 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json");

            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write(postData);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String result = streamToString(urlConn.getInputStream());
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject != null && jsonObject.has("content")) {
                    datas = SVJsonParser.fromJson(jsonObject.getString("content"), new TypeToken<List<SVTimeWeatherInfo>>() {
                    }.getType());
                }

                Log.e(TAG, "Post方式请求成功，result--->" + result);
            } else {
                Log.e(TAG, "Post方式请求失败"+urlConn.getResponseCode());
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return datas;
    }



    public static String getAirQuality(int aqi){
        if(aqi >= 0 && aqi <= 50){
            return "优";
        }else if(aqi > 50 && aqi <= 100){
            return "良";
        }else{
            return "差";
        }
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static double abandonDecimals(double value,int pos) {
        String des = "%." + pos + "f";
        return Double.parseDouble(String.format(des, value));
    }
}
