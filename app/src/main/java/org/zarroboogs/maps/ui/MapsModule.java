package org.zarroboogs.maps.ui;

import android.view.MotionEvent;

import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsActivity;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsModule implements IGaoDeMapsView, AMap.OnMapLoadedListener , AMap.OnMapTouchListener{
    private MapsActivity mMapsActivity;
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private MapsPresenter mMapsPresenter;
    private UiSettings mUiSetting;


    public MapsModule(MapsActivity mapsActivity) {
        this.mMapsActivity = mapsActivity;

        mMapsPresenter = new MapsPresenterImpl(this);
        mMapsActivity.getGaoDeMap().setOnMapLoadedListener(this);
        mMapsActivity.getGaoDeMap().setOnMapTouchListener(this);
        mUiSetting = mMapsActivity.getGaoDeMap().getUiSettings();
    }

    public void init(){
        mUiSetting.setCompassEnabled(false);
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
    public void changeMyLocationMode(int mode) {
        mMapsActivity.getGaoDeMap().setMyLocationType(mode);
    }

    @Override
    public void stopFollowMode() {
        mMapsActivity.getGaoDeMap().setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    public void onMapLoaded() {
        mMapsPresenter.loadDefaultCameraMarkers();
        mMapsPresenter.enableDefaultGeoFences();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP ){
            mMapsPresenter.changeMyLocationMode(AMap.LOCATION_TYPE_MAP_ROTATE);
        }
    }
}
