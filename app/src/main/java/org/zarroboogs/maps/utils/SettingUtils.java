package org.zarroboogs.maps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.maps.AMap;

import org.zarroboogs.maps.MapsApplication;

/**
 * Created by andforce on 15/8/1.
 */
public class SettingUtils {

    private static final String MYLOCATION_KEY = "location_mode";
    private static final String MAPS_STYLE_KEY = "maps_style_key";
    private static final String JING_CAMERA = "jing_camera";
    public static int SWITCH_ON = 1;
    public static int SWITCH_OFF = 0;

    public static int readCurrentMyLocationMode() {
        return FileUtils.readIntFromSharedPreference(MYLOCATION_KEY, AMap.LOCATION_TYPE_MAP_FOLLOW);
    }

    public static void writeCurrentMyLocationMode(int mode) {
        FileUtils.writeIntToSharedPreference(MYLOCATION_KEY, mode);
    }

    public static int readCurrentMapsStyle() {
        return FileUtils.readIntFromSharedPreference(MAPS_STYLE_KEY, AMap.MAP_TYPE_NORMAL);
    }

    public static void writeCurrentMapsStyle(int style) {
        FileUtils.writeIntToSharedPreference(MAPS_STYLE_KEY, style);
    }

    public static int readCurrentCameraState() {
        return FileUtils.readIntFromSharedPreference(JING_CAMERA, SWITCH_OFF);
    }

    public static void writeCurrentCameraState(int state) {
        FileUtils.writeIntToSharedPreference(JING_CAMERA, state);
    }


    private static SharedPreferences sPref = MapsApplication.getAppContext().getSharedPreferences(MapsApplication.getAppContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
    public static final String SETTING_PREF_JING_CAMERA = "setting_pref_jing_camera";
    public static final String SETTING_PREF_JING_CAMERA_ALERT = "setting_pref_jing_camera_alert";

    public static boolean isEnableBeijingCamera(){
        return sPref.getBoolean(SETTING_PREF_JING_CAMERA, true);
    }

    public static boolean isEnableBeijingCameraAlert(){
        return sPref.getBoolean(SETTING_PREF_JING_CAMERA_ALERT, true);
    }


}
