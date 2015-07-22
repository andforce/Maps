package org.zarroboogs.maps.ui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsActivity;
import org.zarroboogs.maps.R;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsModule implements IGaoDeMapsView, AMap.OnMapLoadedListener , AMap.OnMapTouchListener, LocationSource, AMapLocationListener , View.OnClickListener{
    private MapsActivity mMapsActivity;
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private MapsPresenter mMapsPresenter;
    private UiSettings mUiSetting;
    private OnLocationChangedListener mOnLocationChangeListener;
    private LocationManagerProxy mAMapLocationManager;
    private boolean mIsEnableMyLocation = true;
    private AMapLocation mLocation;

    public MapsModule(MapsActivity mapsActivity) {
        this.mMapsActivity = mapsActivity;

        mMapsPresenter = new MapsPresenterImpl(this);
        mMapsActivity.getGaoDeMap().setOnMapLoadedListener(this);
        mMapsActivity.getGaoDeMap().setOnMapTouchListener(this);
        // location
        mMapsActivity.getGaoDeMap().setLocationSource(this);
        mMapsActivity.getGaoDeMap().setMyLocationEnabled(true);

        mMapsActivity.getMyLocationBtn().setOnClickListener(this);

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
        mMapsActivity.getGaoDeMap().setMyLocationEnabled(false);
    }

    @Override
    public void onMapLoaded() {
        mMapsPresenter.loadDefaultCameraMarkers();
        mMapsPresenter.enableDefaultGeoFences();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            mIsEnableMyLocation = false;
        }
    }



    // Location start
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangeListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this.mMapsActivity.getApplicationContext());
            /*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }

    }

    @Override
    public void deactivate() {
        mOnLocationChangeListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mOnLocationChangeListener != null && mOnLocationChangeListener != null) {
            if ((mLocation == null || (mLocation.getLatitude() != aMapLocation.getLatitude() || mLocation.getLongitude() != aMapLocation.getLongitude()))) {
                Log.d("MapsAction", "onLocationChanged");
                if (mIsEnableMyLocation) {
                    mOnLocationChangeListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                mLocation = aMapLocation;
            }
        }
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.my_location_btn){
            mIsEnableMyLocation = true;
            if (mOnLocationChangeListener != null && mLocation != null) {
                mOnLocationChangeListener.onLocationChanged(mLocation);// 显示系统小蓝点
            }
        }
    }
    // Location end
}
