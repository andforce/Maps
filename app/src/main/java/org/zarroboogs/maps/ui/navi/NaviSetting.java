package org.zarroboogs.maps.ui.navi;

import android.content.Context;
import android.content.SharedPreferences;

import org.zarroboogs.maps.MapsApplication;

/**
 * Created by andforce on 15/8/22.
 */
public class NaviSetting {

    public static final String SETTING_PREF_NAVI_NIGHT = "setting_pref_navi_night";
    public static final String SETTING_PREF_NAVI_FIX_PATH = "setting_pref_navi_fix_path";
    public static final String SETTING_PREF_NAVI_JAM = "setting_pref_navi_jam";

    public static final String SETTING_PREF_NAVI_TRAFFIC = "setting_pref_navi_traffic";
    public static final String SETTING_PREF_NAVI_CAMERA = "setting_pref_navi_camera";
    public static final String SETTING_PREF_NAVI_SCREEN_ON = "setting_pref_navi_screen_on";

    public static final String SETTING_PREF_NAVI_BEIJNG_CAMERA = "setting_pref_navi_beijng_camera";

    private static SharedPreferences sPref = MapsApplication.getAppContext().getSharedPreferences(MapsApplication.getAppContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);

    public static boolean getNaviNight(){
        return sPref.getBoolean(SETTING_PREF_NAVI_NIGHT, false);
    }

    public static boolean getReCalculateRouteForYaw(){
        return sPref.getBoolean(SETTING_PREF_NAVI_FIX_PATH, true);
    }

    public static boolean getReCalculateRouteForTrafficJam(){
        return sPref.getBoolean(SETTING_PREF_NAVI_JAM, true);
    }

    public static boolean getTrafficInfoUpdateEnabled(){
        return sPref.getBoolean(SETTING_PREF_NAVI_TRAFFIC, true);
    }

    public static boolean getCameraInfoUpdateEnabled(){
        return sPref.getBoolean(SETTING_PREF_NAVI_CAMERA, true);
    }

    public static boolean getScreenAlwaysBright(){
        return sPref.getBoolean(SETTING_PREF_NAVI_SCREEN_ON, true);
    }

    public static boolean getBeijingCamera(){
        return sPref.getBoolean(SETTING_PREF_NAVI_BEIJNG_CAMERA, true);
    }

}
