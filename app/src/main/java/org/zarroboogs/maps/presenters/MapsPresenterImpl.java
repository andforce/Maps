package org.zarroboogs.maps.presenters;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.beans.BJCamera;
import org.zarroboogs.maps.beans.GeoFenceInfo;
import org.zarroboogs.maps.module.GeoFenceManager;
import org.zarroboogs.maps.presenters.iviews.IGaoDeMapsView;
import org.zarroboogs.maps.presenters.MarkerInteractor.OnMarkerCreatedListener;
import org.zarroboogs.maps.utils.SettingUtils;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsPresenterImpl implements MapsPresenter, OnMarkerCreatedListener, MarkerInteractor.OnReadCamerasListener,
        MapsActionInteractor.OnMyLocationModeChangedListener{

    private static final boolean DEBUG = false;

    private IGaoDeMapsView mGaodeMapsView;
    private MarkerInteractor mMapsInteractor;

    private MapsActionInteractor mMapsActionInteractor;

    private GeoFenceManager geoFenceManager;

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
        if (geoFenceManager != null){
            geoFenceManager.removeAllGeoFenceAlert();
        }
    }

    @Override
    public void changeMyLocationMode() {
        mMapsActionInteractor.changeMyLocationMode(this);
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
    public void onReadCameras(ArrayList<BJCamera> cameraBeans) {
        if (geoFenceManager == null){
            geoFenceManager = new GeoFenceManager(MapsApplication.getAppContext());
        }

        geoFenceManager.requestLocation();

        if (cameraBeans != null){
            ArrayList<GeoFenceInfo> geoFenceInfos = new ArrayList<>();
            for (BJCamera cameraBean : cameraBeans){
                LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
                GeoFenceInfo info = new GeoFenceInfo(MapsApplication.getAppContext(),latLng,cameraBean.getId());
                geoFenceInfos.add(info);
            }

            if (DEBUG){
                GeoFenceInfo geoFenceInfo = new GeoFenceInfo(MapsApplication.getAppContext(), new LatLng(40.09705f, 116.426019f), 100);
                geoFenceInfos.add(geoFenceInfo);
            }

            geoFenceManager.addAllGeoFenceAler(geoFenceInfos);
        }


    }

    @Override
    public void onMyLocationModeChanged(int mode) {
        mGaodeMapsView.changeMyLocationMode(mode);
    }

    @Override
    public void onStopFollowMode() {
        mGaodeMapsView.stopFollowMode();
    }
}
