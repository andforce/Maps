package org.zarroboogs.maps.ui;

import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.db.beans.CameraBean;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public interface MarkerInteractor {

    interface OnMarkerCreatedListener{
        void onMarkerCreated(ArrayList<MarkerOptions> markerOptions);
    }


    interface OnReadCamerasListener{
        void onReadCameras(ArrayList<CameraBean> cameraBeans);
    }
    void createMarkers(OnMarkerCreatedListener listener);

    void readCameras(OnReadCamerasListener listener);

}
