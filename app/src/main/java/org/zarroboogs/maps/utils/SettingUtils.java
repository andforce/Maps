package org.zarroboogs.maps.utils;

import com.amap.api.maps.AMap;

/**
 * Created by andforce on 15/8/1.
 */
public class SettingUtils {

    private static final String MYLOCATION_KEY = "location_mode";
    private static final String MAPS_STYLE_KEY = "maps_style_key";

    public static int readCurrentMyLocationMode(){
        return FileUtils.readIntFromSharedPreference(MYLOCATION_KEY);
    }

    public static void writeCurrentMyLocationMode(int mode){
        FileUtils.writeIntToSharedPreference(MYLOCATION_KEY,mode);
    }

    public static int readCurrentMapsStyle(){
        int i = FileUtils.readIntFromSharedPreference(MAPS_STYLE_KEY);
        if (i == -1){
            return AMap.MAP_TYPE_NORMAL;
        }
        return i;
    }

    public static void writeCurrentMapsStyle(int style){
        FileUtils.writeIntToSharedPreference(MAPS_STYLE_KEY, style);
    }
}
