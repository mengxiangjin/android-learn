package com.jin.weather_pager.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class SVWeatherInfo implements Serializable {
    private String skyconDesc;
    private String date;
    private String avg;
    private String chnAqi;
    private String daySkycon;
    private String nightSkycon;
    private String min;
    private String max;
    private String nightSkyconDesc;
    private String skycon;
    private String daySkyconDesc;
    private String minWindLevel;
    private String coldRisk;
    private String carWashing;
    private String comfort;
    private String ultravioletIndex;
    private String humidityAvg;
    private String visibilityAvg;
    private String dressing;
    private String maxWindSpeed;
    private String minWindSpeed;
    private String ultravioletDesc;
    private String maxWindLevel;
    private String pressureAvg;


    public String getMinWindLevel() {
        return minWindLevel;
    }

    public void setMinWindLevel(String minWindLevel) {
        this.minWindLevel = minWindLevel;
    }

    public String getColdRisk() {
        return coldRisk;
    }

    public void setColdRisk(String coldRisk) {
        this.coldRisk = coldRisk;
    }

    public String getCarWashing() {
        return carWashing;
    }

    public void setCarWashing(String carWashing) {
        this.carWashing = carWashing;
    }

    public String getComfort() {
        return comfort;
    }

    public void setComfort(String comfort) {
        this.comfort = comfort;
    }

    public String getUltravioletIndex() {
        return ultravioletIndex;
    }

    public void setUltravioletIndex(String ultravioletIndex) {
        this.ultravioletIndex = ultravioletIndex;
    }

    public String getHumidityAvg() {
        return humidityAvg;
    }

    public void setHumidityAvg(String humidityAvg) {
        this.humidityAvg = humidityAvg;
    }

    public String getVisibilityAvg() {
        return visibilityAvg;
    }

    public void setVisibilityAvg(String visibilityAvg) {
        this.visibilityAvg = visibilityAvg;
    }

    public String getDressing() {
        return dressing;
    }

    public void setDressing(String dressing) {
        this.dressing = dressing;
    }

    public String getMaxWindSpeed() {
        return maxWindSpeed;
    }

    public void setMaxWindSpeed(String maxWindSpeed) {
        this.maxWindSpeed = maxWindSpeed;
    }

    public String getMinWindSpeed() {
        return minWindSpeed;
    }

    public void setMinWindSpeed(String minWindSpeed) {
        this.minWindSpeed = minWindSpeed;
    }

    public String getUltravioletDesc() {
        return ultravioletDesc;
    }

    public void setUltravioletDesc(String ultravioletDesc) {
        this.ultravioletDesc = ultravioletDesc;
    }

    public String getMaxWindLevel() {
        return maxWindLevel;
    }

    public void setMaxWindLevel(String maxWindLevel) {
        this.maxWindLevel = maxWindLevel;
    }

    public String getPressureAvg() {
        return pressureAvg;
    }

    public void setPressureAvg(String pressureAvg) {
        this.pressureAvg = pressureAvg;
    }

    public String getDaySkycon() {
        return daySkycon;
    }

    public void setDaySkycon(String daySkycon) {
        this.daySkycon = daySkycon;
    }

    public String getNightSkycon() {
        return nightSkycon;
    }

    public void setNightSkycon(String nightSkycon) {
        this.nightSkycon = nightSkycon;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getNightSkyconDesc() {
        return nightSkyconDesc;
    }

    public void setNightSkyconDesc(String nightSkyconDesc) {
        this.nightSkyconDesc = nightSkyconDesc;
    }

    public String getSkycon() {
        return skycon;
    }

    public void setSkycon(String skycon) {
        this.skycon = skycon;
    }

    public String getDaySkyconDesc() {
        return daySkyconDesc;
    }

    public void setDaySkyconDesc(String daySkyconDesc) {
        this.daySkyconDesc = daySkyconDesc;
    }

    public String getSkyconDesc() {
        return skyconDesc;
    }

    public void setSkyconDesc(String skyconDesc) {
        this.skyconDesc = skyconDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvg() {
        return TextUtils.isEmpty(avg)?0+"":((int)(Double.parseDouble(avg)))+"";
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public int getChnAqi() {
        int aqi = TextUtils.isEmpty(chnAqi) ? 0 : Integer.parseInt(chnAqi);
        return aqi;
    }

    @Override
    public String toString() {
        return "SVWeatherInfo{" +
                "skyconDesc='" + skyconDesc + '\'' +
                ", date='" + date + '\'' +
                ", avg='" + avg + '\'' +
                ", chnAqi='" + chnAqi + '\'' +
                ", daySkycon='" + daySkycon + '\'' +
                ", nightSkycon='" + nightSkycon + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", nightSkyconDesc='" + nightSkyconDesc + '\'' +
                ", skycon='" + skycon + '\'' +
                ", daySkyconDesc='" + daySkyconDesc + '\'' +
                ", minWindLevel='" + minWindLevel + '\'' +
                ", coldRisk='" + coldRisk + '\'' +
                ", carWashing='" + carWashing + '\'' +
                ", comfort='" + comfort + '\'' +
                ", ultravioletIndex='" + ultravioletIndex + '\'' +
                ", humidityAvg='" + humidityAvg + '\'' +
                ", visibilityAvg='" + visibilityAvg + '\'' +
                ", dressing='" + dressing + '\'' +
                ", maxWindSpeed='" + maxWindSpeed + '\'' +
                ", minWindSpeed='" + minWindSpeed + '\'' +
                ", ultravioletDesc='" + ultravioletDesc + '\'' +
                ", maxWindLevel='" + maxWindLevel + '\'' +
                ", pressureAvg='" + pressureAvg + '\'' +
                '}';
    }

    public void setChnAqi(String chnAqi) {
        this.chnAqi = chnAqi;
    }
}
