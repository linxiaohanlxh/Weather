package com.example.weather.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weather.R;
import com.example.weather.activity.MainActivity;
import com.example.weather.entity.City;
import com.example.weather.entity.CityWeather;
import com.example.weather.entity.Province;
import com.example.weather.activity.showSelectedCityWeather;
import com.example.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    //private ProgressDialog progressDialog;
    private EditText searchEdit;
    private Button searchButton;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /*** 省列表 */
    private List<Province> provinceList;
    /*** 市列表 */
    private List<City> cityList;
    /*** 选中的省份 */
    private Province selectedProvince;
    /*** 选中的城市 */
    private City selectedCity;
    /*** 当前选中的级别 */
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        searchEdit = (EditText) view.findViewById(R.id.search_edit);
        searchButton = (Button) view.findViewById(R.id.search_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    //获取市的citycode准备用于网络查询城市天气
                    String selectedCity_cityCode = selectedCity.getCityCode();
                    //在主界面时的操作
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), showSelectedCityWeather.class);
                        intent.putExtra("selectedCity_cityCode", selectedCity_cityCode);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof showSelectedCityWeather) {
                        //在选择碎片时的操作
                        showSelectedCityWeather  activity = (showSelectedCityWeather) getActivity();
                        activity.getInfoFromWeb(selectedCity_cityCode);
                    }
                }
            }
        });
        //点击返回
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        //点击搜索
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityCode = searchEdit.getText().toString();
                //输入的ID不足七位，提示ID不足七位
                if(cityCode.length() < 9) {
                    Toast.makeText(getContext(), "ID位数不足9位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getActivity() instanceof MainActivity) {
                    //在主界面时的操作
                    Intent intent = new Intent(getActivity(), showSelectedCityWeather.class);
                    intent.putExtra("selectedCity_cityCode", searchEdit.getText().toString());
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof showSelectedCityWeather) {
                    //在选择城市碎片时的操作
                    showSelectedCityWeather  activity = (showSelectedCityWeather) getActivity();
                    activity.getInfoFromWeb(cityCode);
                }
                searchEdit.setText("");
            }
        });
        queryProvinces();
    }

    /*** 查询全国所有的省，优先从数据库查询，如果没有查询到再去city.json上查询 */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //将第position个item显示在listView的最上面一项
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromCityJson("province");
        }
    }

    /*** 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去city.json上查询 */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("pid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            queryFromCityJson("city");
        }
    }


    /*** 从city.json文件中获取省市县的数据 */
    private void queryFromCityJson(final String type) {
        //showProgressDialog();
        Utility utility = new Utility();
        boolean result = utility.handleResponse();
        if (result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //closeProgressDialog();
                    if ("province".equals(type)) {
                        queryProvinces();
                    } else if ("city".equals(type)) {
                        queryCities();
                    }
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //closeProgressDialog();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*** 显示进度对话框 */
//    private void showProgressDialog() {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("正在加载...");
//            progressDialog.setCanceledOnTouchOutside(false);
//        }
//        progressDialog.show();
//    }

//    /*** 关闭进度对话框 */
//    private void closeProgressDialog() {
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
//    }
}

