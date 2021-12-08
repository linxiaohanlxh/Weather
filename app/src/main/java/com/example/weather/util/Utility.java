package com.example.weather.util;

import android.text.TextUtils;

import com.example.weather.entity.City;
import com.example.weather.entity.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utility {

    private String jsonToSting() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = Utility.this.getClass().getClassLoader().
                    getResourceAsStream("assets/" + "city.json");
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //解析city.json中的数据,分为省、市
    public boolean handleResponse(){
        String response = jsonToSting();
        ArrayList<Province> provinces = new ArrayList<>();

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                //先检索出所有的省份保存到数据库和list中
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int pid = jsonObject.getInt("pid");
                    //省的pid=0，借此来检索所有省
                    if(pid == 0){
                        Province province = new Province();
                        province.setId(jsonObject.getInt("id"));
                        province.setProvinceName(jsonObject.getString("city_name"));
                        province.setProvinceCode(jsonObject.getString("city_code"));
                        province.setPid(jsonObject.getInt("pid"));
                        //将省的信息保存到数据库
                        province.save();
                        provinces.add(province);
                    }
                }
                //市保存到相应的数据库
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int pid = jsonObject.getInt("pid");
                    int j;
                    //pid不等于0说明是市或者是县
                    if(pid != 0){
                        //遍历provinces来找到对应的省
                        for(j = 0;j<provinces.size();j++){
                            if(pid == provinces.get(j).getId()){
                                City city = new City();
                                city.setId(jsonObject.getInt("id"));
                                city.setPid(jsonObject.getInt("pid"));
                                city.setCityName(jsonObject.getString("city_name"));
                                city.setCityCode(jsonObject.getString("city_code"));
                                city.save();
                                break;
                            }
                        }
                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
