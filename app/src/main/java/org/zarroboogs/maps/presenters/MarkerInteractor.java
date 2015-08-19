package org.zarroboogs.maps.presenters;

import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.beans.BJCamera;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public interface MarkerInteractor {

    interface OnMarkerCreatedListener {
        void onMarkerCreated(ArrayList<MarkerOptions> markerOptions);
    }


    interface OnReadCamerasListener {
        void onReadCameras(ArrayList<BJCamera> cameraBeans);
    }

    void createMarkers(OnMarkerCreatedListener listener);

    void readCameras(OnReadCamerasListener listener);

}
