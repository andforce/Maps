package org.zarroboogs.maps.presenters;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.beans.BJCamera;
import org.zarroboogs.maps.utils.FileUtils;
import org.zarroboogs.maps.utils.JsonUtils;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MarkerInteractorImpl implements MarkerInteractor {

    @Override
    public void createMarkers(OnMarkerCreatedListener listener) {
        if (null != listener){
            listener.onMarkerCreated(creayeMarkerOptions());
        }
    }

    @Override
    public void readCameras(OnReadCamerasListener listener) {
        if (listener != null) {
            listener.onReadCameras(readCameras());
        }
    }

    private ArrayList<MarkerOptions> creayeMarkerOptions(){
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
        ArrayList<BJCamera> cameraBeans = readCameras();
        for (BJCamera cameraBean : cameraBeans) {
            LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
            MarkerOptions mo = new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_camera_location));
            markerOptionses.add(mo);
        }
        return  markerOptionses;
    }

    private ArrayList<BJCamera> readCameras(){
        ArrayList<BJCamera> cameraBeans = JsonUtils.prasePaperCameras(FileUtils.readStringFromAsset(MapsApplication.getAppContext(), "beijing_paper.json"));
        return cameraBeans;
    }

}
