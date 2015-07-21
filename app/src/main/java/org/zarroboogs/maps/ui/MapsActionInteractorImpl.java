package org.zarroboogs.maps.ui;

import com.amap.api.maps.AMap;

import org.zarroboogs.maps.utils.FileUtils;

/**
 * Created by wangdiyuan on 15-7-21.
 */
public class MapsActionInteractorImpl implements MapsActionInteractor {
    private static final String MYLOCATION_KEY = "location_mode";

    private int readMyLocationMode() {
        int mode = FileUtils.readIntFromSharedPreference(MYLOCATION_KEY);
        if (mode == -1) {
            mode = AMap.LOCATION_TYPE_MAP_FOLLOW;
        }
        return mode;
    }

    @Override
    public void changeMyLocationMode(int mode, OnMyLocationModeChangedListener listener) {
        if (listener != null) {
            FileUtils.writeIntToSharedPreference(MYLOCATION_KEY,mode);
            listener.onMyLocationChanged(mode);
        }
    }

    @Override
    public void stopFollowMode(OnMyLocationModeChangedListener listener) {
        if (listener != null) {
            FileUtils.writeIntToSharedPreference(MYLOCATION_KEY,AMap.LOCATION_TYPE_LOCATE);
            listener.onMyLocationChanged(AMap.LOCATION_TYPE_LOCATE);
        }
    }

}
