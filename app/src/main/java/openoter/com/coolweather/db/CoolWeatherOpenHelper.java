package openoter.com.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    //创建Provider表
    public static final String CREATE_PROVINCE = "create table Province(" +
            "id integer PRIMARY KEY autoincrement," +
            "province_name text," +
            "province_code text" +
            ")";
    //创建city表
    public static final String CREATE_CITY = "create table City(" +
            "id integer PRIMARY KEY autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer" +
            ")";
    //创建county表
    public static final String CREATE_COUNTY = "create table County(" +
            "id integer PRIMARY KEY autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer" +
            ")";
    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); //创建Provider表
        db.execSQL(CREATE_CITY); //创建city表
        db.execSQL(CREATE_COUNTY); //创建county表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
