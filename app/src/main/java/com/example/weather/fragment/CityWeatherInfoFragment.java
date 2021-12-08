package com.example.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weather.R;

public class CityWeatherInfoFragment extends Fragment {
    public TextView titleText;
    public TextView dateText;
    public TextView updateTimeText;
    public TextView temperatureText;
    public TextView humidityText;
    public TextView pm25Text;
    private String cityCode;
    private TextView cityCodeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_info, container, false);
        titleText = view.findViewById(R.id.title_text);
        dateText = view.findViewById(R.id.date_text);
        updateTimeText = view.findViewById(R.id.updateTime_text);
        temperatureText = view.findViewById(R.id.temperature_text);
        humidityText = view.findViewById(R.id.humidity_text);
        pm25Text = view.findViewById(R.id.pm25_text);
        cityCodeText = view.findViewById(R.id.cityCode_text);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            titleText.setText(bundle.getString("province") + bundle.getString("city"));
            dateText.setText("日期：" + bundle.getString("date"));
            updateTimeText.setText("更新时间：" + bundle.getString("updateTime"));
            temperatureText.setText("温度：" + bundle.getString("temperature") + "℃");
            humidityText.setText("湿度：" + bundle.getString("humidity"));
            pm25Text.setText("pm2.5：" + bundle.getString("pm25"));
            cityCodeText.setText("城市ID："+bundle.getString("cityCode"));
            cityCode = bundle.getString("cityCode");
        }
    }

    public String getCity_Code(){
        String str = cityCodeText.getText().toString();
        str = str.substring(str.indexOf('：')+1);
        return str;
    }
}
