package org.zarroboogs.maps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.OnLocationChangedListener;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.presenters.MapsPresenter;
import org.zarroboogs.maps.presenters.MapsPresenterImpl;
import org.zarroboogs.maps.presenters.iviews.IGaoDeMapsView;
import org.zarroboogs.maps.ui.maps.MapsFragment;
import org.zarroboogs.maps.utils.SettingUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsModule implements IGaoDeMapsView, AMap.OnMapLoadedListener, AMap.OnMapTouchListener, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private MapsPresenter mMapsPresenter;
    private UiSettings mUiSetting;
    private LocationManagerProxy mAMapLocationManager;
    private boolean mIsEnableMyLocation = true;
    private AMapLocation mLocation;
    private AMap mGaodeMap;
    private MapsFragment mMapsFragment;
    private Marker marker;
    private List<Marker> mCameras;

    private SharedPreferences mPref;

    private BitmapDescriptor mMyLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation);

    private MyLocationChangedListener myLocationChangedListener = new MyLocationChangedListener();

    public MapsModule(MapsFragment fragment ,AMap map) {
        this.mMapsFragment = fragment;
        this.mGaodeMap = map;

        mPref = fragment.getActivity().getApplicationContext().getSharedPreferences(fragment.getActivity().getPackageName()+ "_preferences", Context.MODE_PRIVATE);
        mPref.registerOnSharedPreferenceChangeListener(this);

        mMapsPresenter = new MapsPresenterImpl(this);
        mGaodeMap.setOnMapLoadedListener(this);
        mGaodeMap.setOnMapTouchListener(this);
        // location
        mGaodeMap.setMyLocationEnabled(true);

        mMapsFragment.getMyLocationBtn().setOnClickListener(this);

        mUiSetting = mGaodeMap.getUiSettings();

        setMaps();
    }

    public void init() {
        mUiSetting.setCompassEnabled(false);
        mUiSetting.setZoomControlsEnabled(false);
        mUiSetting.setMyLocationButtonEnabled(false);
        mUiSetting.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);

        mGaodeMap.setMapType(SettingUtils.readCurrentMapsStyle());

    }

    public void setMaps(){
        mGaodeMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mGaodeMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种

        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(mMapsFragment.getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2000, 10, myLocationChangedListener);
        }

    }

    private int readMyLocationMode() {
        return SettingUtils.readCurrentMyLocationMode();
    }


    public void onOrientationChanged(float ori) {


        if (mLocation != null && marker != null) {
            CameraPosition currentCP = mGaodeMap.getCameraPosition();

            int mode = readMyLocationMode();
            if (mode == AMap.LOCATION_TYPE_MAP_ROTATE) {
                marker.setRotateAngle(0);
                CameraPosition newCP = new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), currentCP.zoom, currentCP.bearing, ori);
                mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), null);
            } else {
                marker.setRotateAngle(ori);
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingUtils.SETTING_PREF_JING_CAMERA_ALERT)){
            if (SettingUtils.isEnableBeijingCameraAlert()){
                mMapsPresenter.enableDefaultGeoFences();
            } else {
                disableAutoLocation();
            }
        }
    }

    class MyLocationChangedListener extends OnLocationChangedListener {

        @Override
        public void onGaodeLocationChanged(AMapLocation aMapLocation) {

            if ((mLocation == null || (mLocation.getLatitude() != aMapLocation.getLatitude() || mLocation.getLongitude() != aMapLocation.getLongitude()))) {
                Log.d("MapsAction", "onLocationChanged");

                if (mIsEnableMyLocation) {

                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    mGaodeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    if (marker == null) {
                        MarkerOptions mMyLocationMarker = new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).icon(mMyLocationIcon);
                        marker = mGaodeMap.addMarker(mMyLocationMarker);
                    } else {
                        marker.setPosition(latLng);
                    }

                }
                mLocation = aMapLocation;
            }

        }

    }


    public void onDestroy() {
        mPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void deactivate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(myLocationChangedListener);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void addMarker(MarkerOptions marker) {
        Marker addedMarker = mGaodeMap.addMarker(marker);

    }

    @Override
    public void addMarkers(ArrayList<MarkerOptions> markers) {
        removeCameras();
        // false 不移动到中心
        mCameras = mGaodeMap.addMarkers(markers, false);
    }

    @Override
    public void removeMarker(int markerId) {

    }

    public AMapLocation getMyLocation(){
        return mLocation;
    }

    @Override
    public void changeMyLocationMode(final int mode) {

        if (mLocation == null){
            return;
        }

        if (mode == AMap.LOCATION_TYPE_MAP_FOLLOW) {
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, 0, 0);
            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), null);

            mMapsFragment.getMyLocationBtn().setImageResource(R.drawable.ic_qu_direction_mylocation);

        } else if (mode == AMap.LOCATION_TYPE_MAP_ROTATE) {
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, 45, mMapsFragment.getDevicesDirection());
            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), null);

            mMapsFragment.getMyLocationBtn().setImageResource(R.drawable.ic_qu_explore);

        } else if (mode == AMap.LOCATION_TYPE_LOCATE){
            CameraPosition currentCP = mGaodeMap.getCameraPosition();
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, currentCP.tilt, currentCP.bearing);
            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), null);

            mMapsFragment.getMyLocationBtn().setImageResource(R.drawable.ic_qu_direction_mylocation_lost);

        }

    }

    @Override
    public void stopFollowMode() {
        mMapsFragment.getMyLocationBtn().setImageResource(R.drawable.ic_qu_direction_mylocation_lost);
    }

    public void disableAutoLocation(){
        mMapsPresenter.stopFollowMode();
    }

    @Override
    public void changeMapStyle(int style) {
        mGaodeMap.setMapType(style);
    }

    @Override
    public void onMapLoaded() {
        if (SettingUtils.readCurrentCameraState() == SettingUtils.SWITCH_ON){
            mMapsPresenter.loadDefaultCameraMarkers();
        }

        if (SettingUtils.isEnableBeijingCameraAlert()){
            mMapsPresenter.enableDefaultGeoFences();
        }

    }

    public void loadCameras(){
        mMapsPresenter.loadDefaultCameraMarkers();
    }

    public void removeCameras(){
        if (mCameras != null && !mCameras.isEmpty()){
            for (Marker marker : mCameras){
                marker.remove();
                marker.destroy();
            }
        }
    }

    private boolean mIsChanged = false;
    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            if (!mIsChanged){
                mIsEnableMyLocation = false;
                mMapsPresenter.stopFollowMode();
                mIsChanged = true;
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            mIsChanged = false;
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.my_location_btn) {
            mMapsPresenter.changeMyLocationMode();
            mIsEnableMyLocation = true;
        }
    }
    // Location end
}
