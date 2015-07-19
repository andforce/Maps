package org.zarroboogs.maps.ui;

import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public interface MarkerInteractor {

    public static interface OnMarkerCreatedListener{
        public void onMarkerCreated(ArrayList<MarkerOptions> markerOptions);
    }

    public void createMarkers(OnMarkerCreatedListener listener);

}
