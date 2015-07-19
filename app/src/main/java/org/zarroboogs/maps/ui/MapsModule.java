package org.zarroboogs.maps.ui;

import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsActivity;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsModule implements IGaoDeMapsView, AMap.OnMapLoadedListener {
    private MapsActivity mMapsActivity;
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private MapsPresenter mMapsPresenter;
    private UiSettings mUiSetting;


    public MapsModule(MapsActivity mapsActivity) {
        this.mMapsActivity = mapsActivity;

        mMapsPresenter = new MapsPresenterImpl(this);
        mMapsActivity.getGaoDeMap().setOnMapLoadedListener(this);
        mUiSetting = mMapsActivity.getGaoDeMap().getUiSettings();
    }

    public void init(){
        mUiSetting.setCompassEnabled(true);
        mUiSetting.setZoomControlsEnabled(false);
        mUiSetting.setMyLocationButtonEnabled(false);

    }

    @Override
    public void addMarker(MarkerOptions marker) {
        Marker addedMarker = mMapsActivity.getGaoDeMap().addMarker(marker);

    }

    @Override
    public void addMarkers(ArrayList<MarkerOptions> markers) {
        // false 不移动到中心
        mMapsActivity.getGaoDeMap().addMarkers(markers, false);
    }

    @Override
    public void removeMarker(int markerId) {

    }

    @Override
    public void onMapLoaded() {
        mMapsPresenter.loadDefaultCameraMarkers();
        mMapsPresenter.enableDefaultGeoFences();
    }
}
