package org.zarroboogs.maps.presenters;

import com.amap.api.maps.AMap;

import org.zarroboogs.maps.utils.SettingUtils;

/**
 * Created by wangdiyuan on 15-7-21.
 */
public class MapsActionInteractorImpl implements MapsActionInteractor {


    private int readMyLocationMode() {
        int mode = SettingUtils.readCurrentMyLocationMode();
        if (mode == AMap.LOCATION_TYPE_MAP_FOLLOW){
            return AMap.LOCATION_TYPE_MAP_ROTATE;
        } else if(mode == AMap.LOCATION_TYPE_MAP_ROTATE){
            return AMap.LOCATION_TYPE_MAP_FOLLOW;
        } else if (mode == AMap.LOCATION_TYPE_LOCATE) {
            return AMap.LOCATION_TYPE_MAP_FOLLOW;
        }
        return AMap.LOCATION_TYPE_MAP_FOLLOW;
    }

    @Override
    public void changeMyLocationMode(OnMyLocationModeChangedListener listener) {
        if (listener != null) {

            int mode = readMyLocationMode();

            SettingUtils.writeCurrentMyLocationMode(mode);
            listener.onMyLocationModeChanged(mode);
        }
    }

    @Override
    public void stopFollowMode(OnMyLocationModeChangedListener listener) {
        if (listener != null) {
            SettingUtils.writeCurrentMyLocationMode(AMap.LOCATION_TYPE_LOCATE);
            listener.onStopFollowMode();
        }
    }
}
