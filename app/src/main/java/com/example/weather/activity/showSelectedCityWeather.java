package com.example.weather.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.entity.CityWeather;
import com.example.weather.fragment.ChooseAreaFragment;
import com.example.weather.fragment.CityWeatherInfoFragment;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.MyFragmentPagerAdapter;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class showSelectedCityWeather extends AppCompatActivity {

    private String selectedCityCode;
    private List<Fragment> fragmentList;
    private ViewPager cityChange_viewPager;
    private MyFragmentPagerAdapter adapter;
    private List<CityWeather> cityWeatherList;
    private Button favorite_button;
    private Button refresh_button;
    private Button allFavorite_button;
    private int startPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_city_weather);
        init();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        init();
//    }

    private void init() {
        favorite_button = findViewById(R.id.favorite_button);
        refresh_button = findViewById(R.id.refresh_button);
        allFavorite_button = findViewById(R.id.allFavorite_button);

        fragmentList = new ArrayList<>();
        cityChange_viewPager = findViewById(R.id.cityChange_viewPager);
        //添加切换城市的碎片
        fragmentList.add(new ChooseAreaFragment());

        //有缓存，从数据库中读取缓存，否则从网络获取
        cityWeatherList = DataSupport.findAll(CityWeather.class);
        if (cityWeatherList.size() > 0) {
            for (CityWeather cityWeather : cityWeatherList) {
                CityWeatherInfoFragment cityWeatherInfoFragment = new CityWeatherInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("cityCode", cityWeather.getCityCode());
                bundle.putString("province", cityWeather.getProvince());
                bundle.putString("city", cityWeather.getCity());
                bundle.putString("temperature", cityWeather.getTemperature());
                bundle.putString("humidity", cityWeather.getHumidity());
                bundle.putString("pm25", cityWeather.getPm25());
                bundle.putString("updateTime", cityWeather.getUpdateTime());
                bundle.putString("date", cityWeather.getDate());
                cityWeatherInfoFragment.setArguments(bundle);

                fragmentList.add(cityWeatherInfoFragment);
            }
        } else {
            selectedCityCode = getIntent().getStringExtra("selectedCity_cityCode");
            getInfoFromWeb(selectedCityCode);
        }

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        cityChange_viewPager.setAdapter(adapter);
        cityChange_viewPager.setCurrentItem(startPosition);  //初始化显示第一个页面

        //判断是否时从showFavorite活动跳转到此活动的
        String position = getIntent().getStringExtra("position");
        if(position != null){
            Log.i("hhhhhhhhhhhhhhhhhhh",getIntent().getStringExtra("position"));
            cityChange_viewPager.setCurrentItem(Integer.parseInt(position+1));
        }

        //viewPager的滚动监听，如果在选择城市界面则隐藏删除、关注、刷新按钮，否则显示
        cityChange_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    favorite_button.setVisibility(View.GONE);
                    refresh_button.setVisibility(View.GONE);
                    allFavorite_button.setVisibility(View.GONE);
                }else if(position > 0){
                    favorite_button.setVisibility(View.VISIBLE);
                    refresh_button.setVisibility(View.VISIBLE);
                    allFavorite_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //点击取消关注按钮，从数据库中删除，从viewPager将fragment删除
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite_button.getText().toString().equals("取消关注")){
                    //获取碎片的位置
                    int position = cityChange_viewPager.getCurrentItem();
                    //从数据库中删除
                    CityWeatherInfoFragment fragment = (CityWeatherInfoFragment) fragmentList.get(position);
                    DataSupport.deleteAll(CityWeather.class,"citycode = ?",fragment.getCity_Code());
                    //从viewPager将fragment删除
                    fragmentList.remove(position);
                    adapter.updateData(fragmentList);
                    Toast.makeText(showSelectedCityWeather.this, "取消成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击刷新按钮
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityWeatherInfoFragment fragment = (CityWeatherInfoFragment) fragmentList.get(cityChange_viewPager.getCurrentItem());
                getInfoFromWeb(fragment.getCity_Code());
            }
        });
        //点击我的关注按钮，跳转到展示全部关注城市的活动
        allFavorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(showSelectedCityWeather.this,showFavorite.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //从网络获取数据来解析
    public void getInfoFromWeb(String selectedCityCode) {
        //通过网络获取天气情况
        HttpUtil.sendOkHttpRequest("http://t.weather.itboy.net/api/weather/city/" + selectedCityCode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showSelectedCityWeather.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(showSelectedCityWeather.this, "找不到该城市", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                CityWeather cityWeather = HttpUtil.parseJSONWithJSONObject(str);
                Log.i("天气预报", cityWeather.toString());
                if (cityWeather.getProvince() != null) {
                    //打印解析的数据
                    showSelectedCityWeather.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //如果数据库中有这个城市，则更新数据库，否则添加到数据库
                            if(DataSupport.where("citycode=?",selectedCityCode).find(CityWeather.class).size()>0){
                                //当前界面是选择城市界面，则说明已关注该城市，直接跳转到该城市的天气界面，否则在天气界面，说明点击了刷新
                                int position = cityChange_viewPager.getCurrentItem();//跳转页面的positon,默认是当前界面
                                if(cityChange_viewPager.getCurrentItem() == 0){
                                    for(int i=1;i<fragmentList.size();i++){
                                        //遍历找到选择城市的position
                                        if(((CityWeatherInfoFragment) fragmentList.get(i)).getCity_Code().equals(selectedCityCode)){
                                            position = i;
                                            cityChange_viewPager.setCurrentItem(position);
                                            break;
                                        }
                                    }
                                }else{
                                    //更新数据库数据
                                    ContentValues values = new ContentValues();
                                    values.put("date",cityWeather.getDate());
                                    values.put("humidity",cityWeather.getHumidity());
                                    values.put("pm25",cityWeather.getPm25());
                                    values.put("temperature",cityWeather.getTemperature());
                                    values.put("updatetime",cityWeather.getUpdateTime());
                                    DataSupport.updateAll(CityWeather.class,values,"citycode=?",selectedCityCode);
                                    //更新组件的数据
                                    CityWeatherInfoFragment fragment = (CityWeatherInfoFragment) fragmentList.get(position);
                                    fragment.dateText.setText("日期："+cityWeather.getDate());
                                    fragment.updateTimeText.setText("更新时间："+cityWeather.getUpdateTime());
                                    fragment.temperatureText.setText("温度："+cityWeather.getTemperature()+"℃");
                                    fragment.humidityText.setText("湿度："+cityWeather.getHumidity());
                                    fragment.pm25Text.setText("pm2.5："+cityWeather.getPm25());
                                    Toast.makeText(showSelectedCityWeather.this, "刷新成功", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                //将从网络上获取的信息缓存到数据库
                                cityWeather.save();
                                //显示天气预报
                                CityWeatherInfoFragment cityWeatherInfoFragment = new CityWeatherInfoFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("cityCode", cityWeather.getCityCode());
                                bundle.putString("province", cityWeather.getProvince());
                                bundle.putString("city", cityWeather.getCity());
                                bundle.putString("temperature", cityWeather.getTemperature());
                                bundle.putString("humidity", cityWeather.getHumidity());
                                bundle.putString("pm25", cityWeather.getPm25());
                                bundle.putString("updateTime", cityWeather.getUpdateTime());
                                bundle.putString("date", cityWeather.getDate());
                                cityWeatherInfoFragment.setArguments(bundle);

                                fragmentList.add(cityWeatherInfoFragment);
                                adapter.updateData(fragmentList);
                                cityChange_viewPager.setCurrentItem(adapter.getCount());
                                //Toast.makeText(showSelectedCityWeather.this, "加载成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    showSelectedCityWeather.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(showSelectedCityWeather.this, "不存在对应城市", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}