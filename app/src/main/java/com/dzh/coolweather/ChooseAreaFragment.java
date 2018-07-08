package com.dzh.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dzh.coolweather.bean.City;
import com.dzh.coolweather.bean.County;
import com.dzh.coolweather.bean.Province;
import com.dzh.coolweather.util.HttpUtil;
import com.dzh.coolweather.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by mocking on 2018/7/8.
 */

public class ChooseAreaFragment extends Fragment {

    private static final Integer LEVEL_PROVINCE = 0;

    private static final Integer LEVEL_CITY = 1;

    private static final Integer LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView title_tv;

    private Button back_btn;

    private ListView area_lv;

    private ArrayAdapter<String> areaAdapter;

    private List<String> areaDataList = new ArrayList<>(0);

    private List<Province> provinceList = new ArrayList<>(0);

    private List<City> cityList = new ArrayList<>(0);

    private List<County> countyList = new ArrayList<>(0);

    private Province selectedProvince;

    private City selectedCity;

    private County selectedCounty;

    private Integer currentSelectedLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        title_tv = view.findViewById(R.id.tv_title);
        back_btn = view.findViewById(R.id.btn_back);
        area_lv = view.findViewById(R.id.lv_area);
        areaAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,areaDataList);
        area_lv.setAdapter(areaAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        area_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentSelectedLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                }else if (currentSelectedLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSelectedLevel == LEVEL_COUNTY){
                    queryCities();
                }else{
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果没有查到则到服务器查询
     */
    private void queryProvinces(){
        title_tv.setText("中国");
        back_btn.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0){
            areaDataList.clear();
            for(Province province : provinceList){
                areaDataList.add(province.getProvinceName());
            }
            areaAdapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentSelectedLevel = LEVEL_PROVINCE;
        }else {
            String url = "http://guolin.tech/api/china";
            queryFromServer(url,"province");
        }
    }

    /**
     * 查询选中省份的所有城市，先从数据库查询，若没查到则到服务器上查询
     */
    private void queryCities(){
        title_tv.setText(selectedProvince.getProvinceName());
        back_btn.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            areaDataList.clear();
            for(City city : cityList){
                areaDataList.add(city.getCityName());
            }
            areaAdapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentSelectedLevel = LEVEL_CITY;
        }else {
            Integer provinceCode = selectedProvince.getProvinceCode();
            String url = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(url,"city");
        }
    }

    /**
     * 查询所选城市的所有县（区），先从数据库查询，若没找到则到服务器查询
     */
    private void queryCounties(){
        title_tv.setText(selectedCity.getCityName());
        back_btn.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            areaDataList.clear();
            for (County county : countyList){
                areaDataList.add(county.getCountyName());
            }
            areaAdapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentSelectedLevel = LEVEL_COUNTY;
        }else {
            Integer provinceCode = selectedProvince.getProvinceCode();
            Integer cityCode = selectedCity.getCityCode();
            String url = "http://guolin.tech/api/china/" + provinceCode + "/"+ cityCode;
            queryFromServer(url,"county");
        }
    }

    /**
     * 根据传入的url地址和地域类型从服务器上查询地域数据
     * @param url 地域数据网络请求的url
     * @param areaType 省份/市/县（区）
     */
    private void queryFromServer(String url, final String areaType){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseArea = response.body().string();
                boolean handleResult = false;
                if ("province".equals(areaType)){
                    handleResult = Utility.handleProvinceResponse(responseArea);
                }else if("city".equals(areaType)){
                    handleResult = Utility.handleCityResponse(responseArea,selectedProvince.getId());
                }else if("county".equals(areaType)){
                    handleResult = Utility.handleCountyResponse(responseArea,selectedCity.getId());
                }
                if (handleResult){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(areaType)){
                                queryProvinces();
                            }else if("city".equals(areaType)){
                                queryCities();
                            }else if("county".equals(areaType)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载'");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
