package org.zarroboogs.maps.presenters;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.beans.BJCamera;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MarkerInteractorImpl implements MarkerInteractor {

    private BitmapDescriptor mMarkerDesc = BitmapDescriptorFactory.fromResource(R.drawable.icon_camera_location);
    @Override
    public void createMarkers(OnMarkerCreatedListener listener) {
        if (null != listener){
            listener.onMarkerCreated(createMarkerOptions());
        }
    }

    @Override
    public void readCameras(OnReadCamerasListener listener) {
        if (listener != null) {
            listener.onReadCameras(readCameras());
        }
    }

    private ArrayList<MarkerOptions> createMarkerOptions(){
        ArrayList<MarkerOptions> markerOptions = new ArrayList<>();
        ArrayList<BJCamera> cameraBeans = readCameras();
        for (BJCamera cameraBean : cameraBeans) {
            LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
            MarkerOptions mo = new MarkerOptions().position(latLng).draggable(true).icon(mMarkerDesc);
            markerOptions.add(mo);
        }
        return  markerOptions;
    }

    private ArrayList<BJCamera> readCameras(){
        return (ArrayList<BJCamera>) MapsApplication.getDaoSession().loadAll(BJCamera.class);
    }

}
