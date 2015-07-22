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
        if (mode == AMap.LOCATION_TYPE_LOCATE){
            return AMap.LOCATION_TYPE_MAP_FOLLOW;
        } else if (mode == AMap.LOCATION_TYPE_MAP_FOLLOW){
            return AMap.LOCATION_TYPE_MAP_ROTATE;
        } else if(mode == AMap.LOCATION_TYPE_MAP_ROTATE){
            return AMap.LOCATION_TYPE_LOCATE;
        } else {
            return AMap.LOCATION_TYPE_MAP_FOLLOW;
        }
    }

    @Override
    public void changeMyLocationMode(OnMyLocationModeChangedListener listener) {
        if (listener != null) {

            int mode = readMyLocationMode();

            FileUtils.writeIntToSharedPreference(MYLOCATION_KEY,mode);
            listener.onMyLocationModeChanged(mode);
        }
    }

    @Override
    public void stopFollowMode(OnMyLocationModeChangedListener listener) {
        if (listener != null) {
            FileUtils.writeIntToSharedPreference(MYLOCATION_KEY,AMap.LOCATION_TYPE_LOCATE);
            listener.onMyLocationModeChanged(AMap.LOCATION_TYPE_LOCATE);
        }
    }

}
