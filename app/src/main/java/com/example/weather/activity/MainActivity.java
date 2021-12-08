package com.example.weather.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.weather.R;
import com.example.weather.entity.CityWeather;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<CityWeather> cityWeatherList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityWeatherList = DataSupport.findAll(CityWeather.class);
        if (cityWeatherList.size() > 0) {
            Intent intent = new Intent(this, showSelectedCityWeather.class);
            startActivity(intent);
            finish();
        }
        init();
    }

    private void init() {

    }

}