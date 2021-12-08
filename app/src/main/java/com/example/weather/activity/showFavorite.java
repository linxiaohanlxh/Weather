package com.example.weather.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.entity.CityWeather;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class showFavorite extends AppCompatActivity {

    private ListView allFavoriteLv;
    private List<String> dataList = new ArrayList<>();
    private List<CityWeather> cityWeatherList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favorite);
        init();
    }

    private void init() {
        allFavoriteLv = (ListView) findViewById(R.id.allFavorite_listview);
        adapter = new ArrayAdapter<String>(
                showFavorite.this, android.R.layout.simple_list_item_1, dataList);
        allFavoriteLv.setAdapter(adapter);

        cityWeatherList = DataSupport.findAll(CityWeather.class);
        if (cityWeatherList.size() > 0) {
            for (int i = 0; i < cityWeatherList.size(); i++) {
                dataList.add(cityWeatherList.get(i).getProvince() + cityWeatherList.get(i).getCity()
                        + "(" + cityWeatherList.get(i).getCityCode() + ")");
            }
        }
        //点击item 跳转城市的天气界面，根据传送的citycode值在view——pager中找到对应的碎片
        allFavoriteLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(showFavorite.this,showSelectedCityWeather.class);
                intent.putExtra("position",position+"");
                startActivity(intent);
                finish();
            }
        });
        //长按item实现删除
        allFavoriteLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(showFavorite.this);
                dialog.setTitle("删除");
                dialog.setMessage("是否要删除此城市？");
                //通过back取消
                dialog.setCancelable(true);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.deleteAll(CityWeather.class,"citycode=?",cityWeatherList.get(position).getCityCode());
                        Toast.makeText(showFavorite.this, "删除成功", Toast.LENGTH_SHORT).show();
                        cityWeatherList.remove(position);
                        dataList.remove(position);
                        adapter.notifyDataSetChanged();
                        //如果listview中没有东西，跳转到主界面，重新开始搜索
                        if(dataList.size() <= 0){
                            Intent intent = new Intent(showFavorite.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(showFavorite.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}