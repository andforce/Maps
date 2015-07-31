package org.zarroboogs.maps.ui;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.MapsFragment;
import org.zarroboogs.maps.OnLocationChangedListener;
import org.zarroboogs.maps.R;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsModule implements IGaoDeMapsView, AMap.OnMapLoadedListener, AMap.OnMapTouchListener, View.OnClickListener {
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private MapsPresenter mMapsPresenter;
    private UiSettings mUiSetting;
    private LocationManagerProxy mAMapLocationManager;
    private boolean mIsEnableMyLocation = true;
    private boolean mIsFirstLocation = true;
    private AMapLocation mLocation;
    private AMap mGaodeMap;
    private MapsFragment mMapsFragment;
    private MarkerOptions mMyLocationMarker;

    private MyLocationChangedListener myLocationChangedListener;

    public MapsModule(MapsFragment fragment ,AMap map) {
        this.mMapsFragment = fragment;
        this.mGaodeMap = map;

        myLocationChangedListener = new MyLocationChangedListener();

        mMapsPresenter = new MapsPresenterImpl(this);
        mGaodeMap.setOnMapLoadedListener(this);
        mGaodeMap.setOnMapTouchListener(this);
        // location
        mGaodeMap.setMyLocationEnabled(true);

        mMapsFragment.getMyLocationBtn().setOnClickListener(this);

        mUiSetting = mGaodeMap.getUiSettings();

        activateLocation();
    }

    public void init() {
        mUiSetting.setCompassEnabled(false);
        mUiSetting.setZoomControlsEnabled(false);
        mUiSetting.setMyLocationButtonEnabled(false);
        mUiSetting.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);

    }

    @Override
    public void addMarker(MarkerOptions marker) {
        Marker addedMarker = mGaodeMap.addMarker(marker);

    }

    @Override
    public void addMarkers(ArrayList<MarkerOptions> markers) {
        // false 不移动到中心
        mGaodeMap.addMarkers(markers, false);
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
            CameraPosition currentCP = mGaodeMap.getCameraPosition();
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, currentCP.tilt, mMapsFragment.getDevicesDirection());

            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mGaodeMap.setMyLocationType(mode);
                }

                @Override
                public void onCancel() {
                    mGaodeMap.setMyLocationType(mode);
                }
            });

        } else if (mode == AMap.LOCATION_TYPE_MAP_ROTATE) {
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, 45, mMapsFragment.getDevicesDirection());
            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mGaodeMap.setMyLocationType(mode);
                }

                @Override
                public void onCancel() {
                    mGaodeMap.setMyLocationType(mode);
                }
            });
        } else if (mode == AMap.LOCATION_TYPE_LOCATE){
            CameraPosition currentCP = mGaodeMap.getCameraPosition();
            CameraPosition newCP= new CameraPosition(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),15, currentCP.tilt, currentCP.bearing);
            mGaodeMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCP), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mGaodeMap.setMyLocationType(mode);
                }

                @Override
                public void onCancel() {
                    mGaodeMap.setMyLocationType(mode);
                }
            });
        }

    }

    @Override
    public void stopFollowMode() {
    }

    @Override
    public void onMapLoaded() {
        mMapsPresenter.loadDefaultCameraMarkers();
        mMapsPresenter.enableDefaultGeoFences();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mIsEnableMyLocation = false;
            mMapsPresenter.stopFollowMode();
        }
    }


    // Location start
    public void activateLocation() {
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this.mMapsFragment.getActivity().getApplicationContext());
        }

              /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
        mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10, myLocationChangedListener);
    }

    class MyLocationChangedListener extends OnLocationChangedListener {

        @Override
        public void onGaodeLocationChanged(AMapLocation aMapLocation) {
            if ((mLocation == null || (mLocation.getLatitude() != aMapLocation.getLatitude() || mLocation.getLongitude() != aMapLocation.getLongitude()))) {
                Log.d("MapsAction", "onLocationChanged");
                if (mIsEnableMyLocation) {
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    if (mMyLocationMarker == null){
                        mMyLocationMarker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_qu_explore_here_white));
                        mGaodeMap.addMarker(mMyLocationMarker);
                    } else {
                        mMyLocationMarker.position(latLng);
                    }

                }
                mLocation = aMapLocation;

                if (mIsFirstLocation){
                    mMapsPresenter.changeMyLocationMode();
                    mIsFirstLocation = false;
                }
            }
        }

    }

    public void deactivate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(myLocationChangedListener);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
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
