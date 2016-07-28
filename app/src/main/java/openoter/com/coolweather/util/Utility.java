package openoter.com.coolweather.util;

import android.text.TextUtils;

import openoter.com.coolweather.db.CoolWeatherDB;
import openoter.com.coolweather.model.City;
import openoter.com.coolweather.model.County;
import openoter.com.coolweather.model.Province;

/**
 * 解析数据（服务器返回的是“代号|城市,代号|城市”格式的数据）
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String [] allProvince = response.split(",");
            if(allProvince != null && allProvince.length > 0){
                for(String p : allProvince){
                    String [] array = p.split("\\|");

                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);

                    //将数据存到Province表中去
                    coolWeatherDB.saveProvince(province);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String [] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String p : allCities){
                    String [] array = p.split("\\|");

                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将数据存到Province表中去
                    coolWeatherDB.saveCity(city);
                }

                return true;
            }
        }
        return false;
    }


    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String [] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String p : allCounties){
                    String [] array = p.split("\\|");

                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将数据存到Province表中去
                    coolWeatherDB.saveCounty(county);
                }

                return true;
            }
        }
        return false;
    }

}
