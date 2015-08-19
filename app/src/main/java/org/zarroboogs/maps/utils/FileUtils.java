package org.zarroboogs.maps.utils;

import android.content.Context;
import android.content.SharedPreferences;


import org.zarroboogs.maps.MapsApplication;

import java.io.InputStream;

/**
 * Created by wangdiyuan on 15-7-13.
 */
public class FileUtils {
    public static String readStringFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();//取得数据流的数据大小
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String txt = new String(buffer);
            return txt;
        } catch (Exception e) {
        }
        return null;
    }

    private static final SharedPreferences sPreference = MapsApplication.getAppContext().getSharedPreferences(
            MapsApplication.getAppContext().getPackageName(), Context.MODE_PRIVATE);

    public static String readSharedPreference(String key) {
        return sPreference.getString(key, "");
    }

    public static void writeSharedPreference(String key, String value) {
        sPreference.edit().putString(key, value).commit();
    }

    public static int readIntFromSharedPreference(String key, int def) {
        return sPreference.getInt(key, def);
    }

    public static void writeIntToSharedPreference(String key, int value) {
        sPreference.edit().putInt(key, value).commit();
    }

    public static boolean readBooleanFromSharedPreference(String key, boolean def){
        return sPreference.getBoolean(key, def);
    }

    public static void writeBooleanToSharedPreference(String key , boolean value){
        sPreference.edit().putBoolean(key, value).commit();
    }
}
