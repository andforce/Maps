package org.zarroboogs.maps.module;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.model.LatLng;

import org.zarroboogs.maps.beans.GeoFenceInfo;

import java.util.ArrayList;

/**
 * Created by wangdiyuan on 15-7-17.
 */
public class GeoFenceManager {

    private static final String TAG = "GeoFenceManager";

    private ArrayList<GeoFenceInfo> mGeoFences = new ArrayList<>();
    private LocationManagerProxy mLocationManagerProxy;//定位实例
    private Context mContext;

    private OnGeoFenceListener mOnGenFenceListener;

    private LatLng mCurrentLatLng;
    private TTSController ttsController = null;

    public GeoFenceManager(Context context) {
        this.mContext = context;
        ttsController = TTSController.getInstance(mContext);
        mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);

        requestLocation();
    }

    public void requestLocation() {
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次
        //在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 2000, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        Log.d(TAG, "onLocationChanged-> AMAPLocation " + mCurrentLatLng.latitude + " " + mCurrentLatLng.longitude);
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                        Log.d(TAG, "onLocationChanged-> " + mCurrentLatLng.latitude + " " + mCurrentLatLng.longitude);

                        if (mCurrentLatLng == null || mCurrentLatLng.latitude != location.getLatitude()
                                || mCurrentLatLng.longitude != location.getLongitude()){
                            mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        }


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }


    public interface OnGeoFenceListener {
        public void onGeoFenceIn(LatLng currentLatLng, LatLng geoFenceLatLng);

        public void onGeoFenceOut();
    }

    private void addGeoFenceAlert(GeoFenceInfo geofence) {
        LatLng latLng = geofence.getLatLng();
        mLocationManagerProxy.addGeoFenceAlert(latLng.latitude, latLng.longitude, 1000, 1000 * 60 * 60 * 12, geofence.getPendingIntent());
    }

    private void removeGeoFenceAlert(PendingIntent pi) {
        mLocationManagerProxy.removeGeoFenceAlert(pi);
    }

    public void addAllGeoFenceAler(ArrayList<GeoFenceInfo> infos) {

        mGeoFences.addAll(infos);

        for (GeoFenceInfo info : mGeoFences) {
            mLocationManagerProxy.addGeoFenceAlert(info.getLatLng().latitude, info.getLatLng().longitude, 1000,
                    1000 * 60 * 30, info.getPendingIntent());
        }

        registerListener();
    }

    public void removeAllGeoFenceAlert() {
        for (GeoFenceInfo info : mGeoFences) {
            removeGeoFenceAlert(info.getPendingIntent());
        }
        unregisterListener();
    }

    private void registerListener() {
        for (GeoFenceInfo info : mGeoFences) {
            mContext.registerReceiver(mGeoFenceReceiver, info.getFilter());
        }
    }

    private void unregisterListener() {
        mContext.unregisterReceiver(mGeoFenceReceiver);
    }

    public void setOnGeoFenceListener(OnGeoFenceListener listener){
        this.mOnGenFenceListener = listener;
    }

    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive   " + intent.getAction());

            // 接受广播
            if (intent.getAction().contains(GeoFenceInfo.GEOFENCE_BROADCAST_ACTION)) {

                LatLng latLng = null;

                for (GeoFenceInfo geo: mGeoFences){
                    if (geo.getAction().equals(intent.getAction())){
                        latLng = geo.getLatLng();
                    }
                }

                Bundle bundle = intent.getExtras();
                // 根据广播的status来确定是在区域内还是在区域外
                int status = bundle.getInt("status");
                if (status == 0) {
//                    Toast.makeText(mContext, "不在区域", Toast.LENGTH_SHORT).show();
                    if (mOnGenFenceListener != null){
                        mOnGenFenceListener.onGeoFenceIn(mCurrentLatLng,latLng);
                    }
                } else {
                    ttsController.playText("请注意，一公里范围内有进京证摄像头");
                    if (mOnGenFenceListener != null){
                        mOnGenFenceListener.onGeoFenceOut();
                    }
                }
            }

        }
    };
}
