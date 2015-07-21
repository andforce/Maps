package org.zarroboogs.maps.ui;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.beans.GeoFenceInfo;
import org.zarroboogs.maps.db.beans.CameraBean;
import org.zarroboogs.maps.module.GeoFenceManager;
import org.zarroboogs.maps.ui.MarkerInteractor.OnMarkerCreatedListener;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsPresenterImpl implements MapsPresenter, OnMarkerCreatedListener, MarkerInteractor.OnReadCamerasListener, MapsActionInteractor.OnMyLocationModeChangedListener {

    private static final boolean DEBUG = true;

    private IGaoDeMapsView mGaodeMapsView;
    private MarkerInteractor mMapsInteractor;

    private MapsActionInteractor mMapsActionInteractor;

    private GeoFenceManager manager;

    public MapsPresenterImpl(IGaoDeMapsView gaoDeMapsView) {
        this.mGaodeMapsView = gaoDeMapsView;
        this.mMapsInteractor = new MarkerInteractorImpl();
        this.mMapsActionInteractor = new MapsActionInteractorImpl();
    }

    @Override
    public void loadDefaultCameraMarkers() {
        mMapsInteractor.createMarkers(this);
    }

    @Override
    public void enableDefaultGeoFences() {
        mMapsInteractor.readCameras(this);
    }

    @Override
    public void disableDefaultGeoFences() {
        if (manager != null){
            manager.removeAllGeoFenceAlert();
        }
    }

    @Override
    public void changeMyLocationMode(int mode) {
        mMapsActionInteractor.changeMyLocationMode(mode, this);
    }

    @Override
    public void stopFollowMode() {

    }

    @Override
    public void onMarkerCreated(ArrayList<MarkerOptions> markerOptions) {
        mGaodeMapsView.addMarkers(markerOptions);
    }

    @Override
    public void onReadCameras(ArrayList<CameraBean> cameraBeans) {
        if (manager == null){
            manager = new GeoFenceManager(MapsApplication.getAppContext());
        }

        manager.requestLocation();

        if (cameraBeans != null){
            ArrayList<GeoFenceInfo> geoFenceInfos = new ArrayList<>();
            for (CameraBean cameraBean : cameraBeans){
                LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
                GeoFenceInfo info = new GeoFenceInfo(MapsApplication.getAppContext(),latLng,cameraBean.getId());
                geoFenceInfos.add(info);
            }

            if (DEBUG){
                GeoFenceInfo geoFenceInfo = new GeoFenceInfo(MapsApplication.getAppContext(), new LatLng(40.09705f, 116.426019f), 100);
                geoFenceInfos.add(geoFenceInfo);
            }

            manager.addAllGeoFenceAler(geoFenceInfos);
        }


    }

    @Override
    public void onMyLocationChanged(int mode) {
        mGaodeMapsView.changeMyLocationMode(mode);
    }

    @Override
    public void onFollowModeStoped() {
        mGaodeMapsView.stopFollowMode();
    }
}
