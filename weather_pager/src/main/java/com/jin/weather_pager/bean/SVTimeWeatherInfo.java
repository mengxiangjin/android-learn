package com.jin.weather_pager.bean;

import java.io.Serializable;

public class SVTimeWeatherInfo implements Serializable {

    private String dateTime;    //"2023-06-07 16:00"
    private String dayTime;     // "2023-06-07"
    private String description; //"阴，明天凌晨3点钟后转小雨，其后多云"
    private String visibilityValue; //"19.0"
    private String pressureValue;   //"100442.835"
    private String dswrfValue;  //"427.558"
    private String precipitationProbability;    //"0.0"
    private String precipitationValue;
    private String cloudrateValue;  //"1.0"
    private String temperatureValue;    //"29.0"
    private String skyconValue; //"阴"
    private String apparentTemperatureValue;    //"34.1"
    private String humidityValue;   //"0.92"
    private String windSpeed;   //"1级"
    private String windDirection;   //"北风"
    private String airQualityPm25Value;
    private String airQualityApiValue;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibilityValue() {
        return visibilityValue;
    }

    public void setVisibilityValue(String visibilityValue) {
        this.visibilityValue = visibilityValue;
    }

    public String getPressureValue() {
        return pressureValue;
    }

    public void setPressureValue(String pressureValue) {
        this.pressureValue = pressureValue;
    }

    public String getDswrfValue() {
        return dswrfValue;
    }

    public void setDswrfValue(String dswrfValue) {
        this.dswrfValue = dswrfValue;
    }

    public String getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(String precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public String getPrecipitationValue() {
        return precipitationValue;
    }

    public void setPrecipitationValue(String precipitationValue) {
        this.precipitationValue = precipitationValue;
    }

    public String getCloudrateValue() {
        return cloudrateValue;
    }

    public void setCloudrateValue(String cloudrateValue) {
        this.cloudrateValue = cloudrateValue;
    }

    public String getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(String temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public String getSkyconValue() {
        return skyconValue;
    }

    public void setSkyconValue(String skyconValue) {
        this.skyconValue = skyconValue;
    }

    public String getApparentTemperatureValue() {
        return apparentTemperatureValue;
    }

    public void setApparentTemperatureValue(String apparentTemperatureValue) {
        this.apparentTemperatureValue = apparentTemperatureValue;
    }

    public String getHumidityValue() {
        return humidityValue;
    }

    public void setHumidityValue(String humidityValue) {
        this.humidityValue = humidityValue;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getAirQualityPm25Value() {
        return airQualityPm25Value;
    }

    public void setAirQualityPm25Value(String airQualityPm25Value) {
        this.airQualityPm25Value = airQualityPm25Value;
    }

    public String getAirQualityApiValue() {
        return airQualityApiValue;
    }

    public void setAirQualityApiValue(String airQualityApiValue) {
        this.airQualityApiValue = airQualityApiValue;
    }
}
