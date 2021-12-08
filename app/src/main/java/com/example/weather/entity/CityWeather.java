package com.example.weather.entity;

import org.litepal.crud.DataSupport;

public class CityWeather extends DataSupport {
    private int id;
    private String province;
    private String city;
    private String updateTime;
    private String temperature;
    private String humidity;
    private String pm25;
    private String date;
    private String cityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    @Override
    public String toString() {
        return "CityWeather{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", date='" + date + '\'' +
                ", cityCode='" + cityCode + '\'' +
                '}';
    }
}
