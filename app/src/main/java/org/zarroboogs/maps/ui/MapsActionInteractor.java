package org.zarroboogs.maps.ui;

import com.amap.api.location.AMapLocationListener;

/**
 * Created by wangdiyuan on 15-7-21.
 */
public interface MapsActionInteractor {
    interface OnMyLocationModeChangedListener {
        void onMyLocationModeChanged(int mode);
        void onStopFllowMode();
    }

    void changeMyLocationMode(OnMyLocationModeChangedListener listener);

    void stopFollowMode(OnMyLocationModeChangedListener listener);
}
