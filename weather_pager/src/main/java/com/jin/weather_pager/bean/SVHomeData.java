package com.jin.weather_pager.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class SVHomeData implements Serializable {
    private String airQualityNo2;
    private String lng;
    private String lat;
    private String province;
    private String city;
    private String airQualityCo;
    private String district;
    private String airQualityPm25;
    private String airQualitySo2;
    private String airQualityPm10;
    private String airQuality03;
    private ArrayList<SVWeatherInfo> list;

    public String getAirQualityNo2() {
        return airQualityNo2;
    }

    public void setAirQualityNo2(String airQualityNo2) {
        this.airQualityNo2 = airQualityNo2;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        if(!TextUtils.isEmpty(city)){
            return city;
        }else {
            return province;
        }
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAirQualityCo() {
        return airQualityCo;
    }

    public void setAirQualityCo(String airQualityCo) {
        this.airQualityCo = airQualityCo;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAirQualityPm25() {
        return airQualityPm25;
    }

    public void setAirQualityPm25(String airQualityPm25) {
        this.airQualityPm25 = airQualityPm25;
    }

    public String getAirQualitySo2() {
        return airQualitySo2;
    }

    public void setAirQualitySo2(String airQualitySo2) {
        this.airQualitySo2 = airQualitySo2;
    }

    public String getAirQualityPm10() {
        return airQualityPm10;
    }

    public void setAirQualityPm10(String airQualityPm10) {
        this.airQualityPm10 = airQualityPm10;
    }

    public String getAirQuality03() {
        return airQuality03;
    }

    public void setAirQuality03(String airQuality03) {
        this.airQuality03 = airQuality03;
    }

    public ArrayList<SVWeatherInfo> getList() {
        return list;
    }

    public void setList(ArrayList<SVWeatherInfo> list) {
        this.list = list;
    }
}
