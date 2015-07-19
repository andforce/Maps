package org.zarroboogs.maps.ui;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.db.beans.CameraBean;
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
            listener.onMarkerCreated(readCameras());
        }
    }

    private ArrayList<MarkerOptions> readCameras(){
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
        ArrayList<CameraBean> cameraBeans = JsonUtils.prasePaperCameras(FileUtils.readStringFromAsset(MapsApplication.getAppContext(), "beijing_paper.json"));;
        for (CameraBean cameraBean : cameraBeans) {
            LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
            MarkerOptions mo = new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_camera_location));
            markerOptionses.add(mo);
        }
        return  markerOptionses;
    }

}
