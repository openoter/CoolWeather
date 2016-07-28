package openoter.com.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import openoter.com.coolweather.R;
import openoter.com.coolweather.db.CoolWeatherDB;
import openoter.com.coolweather.model.City;
import openoter.com.coolweather.model.County;
import openoter.com.coolweather.model.Province;
import openoter.com.coolweather.util.HttpCallbackListener;
import openoter.com.coolweather.util.HttpUtil;
import openoter.com.coolweather.util.Utility;

/**
 * Created by asus on 2016/7/27.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    //省列表
    private List<Province> provincesList;
    //市列表
    private List<City> cityList;
    //县
    private List<County> countyList;

    //选中的省
    private Province selectProvince;
    //选中的城市
    private City selectCity;
    //当前选中的级别
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        
        initViews();
        initData();

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        initEvent();
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectProvince = provincesList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces(); // 加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，在从服务器查询
     */
    private void queryProvinces() {
        provincesList = coolWeatherDB.loadProvince();
        if (provincesList.size() > 0){
            dataList.clear();
            for (Province province : provincesList){
                dataList.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询所有的县
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounty(selectCity.getId());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectCity.getCityCode(), "county");
        }
    }

    /**
     * 查询所有的市
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCity(selectProvince.getId());
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 从服务器上省市县数据（代号和类型）
     * @param code 代号
     * @param type 类型
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB, response);
                }else  if("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectProvince.getId());
                }else if("county".equals(type)){
                    //result = Utility.handleCountiesResponse(coolWeatherDB, response, selectCity.getId());
                    result = Utility.handleCountiesResponse(coolWeatherDB, response, selectCity.getId());
                    Log.e("Res", String.valueOf(result));
                }


                if(result){
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else  if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    private void initData() {
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}
