package org.zarroboogs.maps.presenters;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.beans.GeoFenceInfo;
import org.zarroboogs.maps.db.beans.CameraBean;
import org.zarroboogs.maps.module.GeoFenceManager;
import org.zarroboogs.maps.ui.IGaoDeMapsView;
import org.zarroboogs.maps.presenters.MarkerInteractor.OnMarkerCreatedListener;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsPresenterImpl implements MapsPresenter, OnMarkerCreatedListener, MarkerInteractor.OnReadCamerasListener,
        MapsActionInteractor.OnMyLocationModeChangedListener, AMapLocationListener{

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
    public void changeMyLocationMode() {
        mMapsActionInteractor.changeMyLocationMode( this);
    }

    @Override
    public void stopFollowMode() {
        mMapsActionInteractor.stopFollowMode(this);
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
    public void onMyLocationModeChanged(int mode) {
        mGaodeMapsView.changeMyLocationMode(mode);
    }

    @Override
    public void onStopFllowMode() {
        mGaodeMapsView.stopFollowMode();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
