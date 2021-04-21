package org.zarroboogs.maps.presenters;

/**
 * Created by wangdiyuan on 15-7-21.
 */
public interface MapsActionInteractor {
    interface OnMyLocationModeChangedListener {
        void onMyLocationModeChanged(int mode);
        void onStopFollowMode();
    }

    void changeMyLocationMode(OnMyLocationModeChangedListener listener);

    void stopFollowMode(OnMyLocationModeChangedListener listener);
}
