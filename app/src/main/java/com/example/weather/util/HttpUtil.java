package com.example.weather.util;

import com.example.weather.entity.CityWeather;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        //发起一条HTTP请求，就需要创建一个Request 对象
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        //调用OkHttpClient的newCall() 方法来创建一个Call 对象，并调用它的execute() 方法 来发送请求并获取服务器返回的数据
        client.newCall(request).enqueue(callback);
    }

    //解析Json格式的数据分装成实体类CityWeather
    public static CityWeather parseJSONWithJSONObject(String jsonData) {
        CityWeather cityWeather = new CityWeather();
        try {
            //解析最外层
            JSONObject jsonObject = new JSONObject(jsonData);
            //获取日期
            cityWeather.setDate(jsonObject.getString("date"));
            //解析cityInfo层
            JSONObject jsonObject_cityInfo = jsonObject.getJSONObject("cityInfo");
            //获取省
            cityWeather.setProvince(jsonObject_cityInfo.getString("parent"));
            //获取市
            cityWeather.setCity(jsonObject_cityInfo.getString("city"));
            //获取更新时间
            cityWeather.setUpdateTime(jsonObject_cityInfo.getString("updateTime"));
            //获取cityCode
            cityWeather.setCityCode(jsonObject_cityInfo.getString("citykey"));

            //解析data层
            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
            //获取温度
            cityWeather.setTemperature(jsonObject_data.getString("wendu"));
            //获取湿度
            cityWeather.setHumidity(jsonObject_data.getString("shidu"));
            //获取pm25
            cityWeather.setPm25(jsonObject_data.getString("pm25"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityWeather;
    }
}
