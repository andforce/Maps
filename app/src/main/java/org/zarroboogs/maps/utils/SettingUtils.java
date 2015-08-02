package org.zarroboogs.maps.utils;

import com.amap.api.maps.AMap;

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
        return FileUtils.readIntFromSharedPreference(MAPS_STYLE_KEY, AMap.LOCATION_TYPE_MAP_FOLLOW);
    }

    public static void writeCurrentMapsStyle(int style) {
        FileUtils.writeIntToSharedPreference(MAPS_STYLE_KEY, style);
    }

    public static int readCurrentCameraState() {
        return FileUtils.readIntFromSharedPreference(JING_CAMERA, SWITCH_ON);
    }

    public static void writeCurrentCameraState(int state) {
        FileUtils.writeIntToSharedPreference(JING_CAMERA, state);
    }
}
