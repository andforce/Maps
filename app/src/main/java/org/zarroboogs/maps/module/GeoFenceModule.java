package org.zarroboogs.maps.module;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;

import org.zarroboogs.maps.beans.GeoFenceInfo;
import org.zarroboogs.maps.db.beans.PaperCameraBean;
import org.zarroboogs.maps.utils.FileUtils;
import org.zarroboogs.maps.utils.JsonUtils;

import java.util.ArrayList;

/**
 * Created by wangdiyuan on 15-7-17.
 */
public class GeoFenceModule {

    public static final String TAG = "GeoFenceModule";
    private Context mContext;
    private MapView mapView;
    private AMap mBaiduMap;

    private GeoFenceManager mGeoFenceManager;


    public GeoFenceModule(MapView mapView) {
        this.mContext = mapView.getContext().getApplicationContext();
        this.mapView = mapView;

        this.mBaiduMap = mapView.getMap();

        mGeoFenceManager = new GeoFenceManager(mContext);


    }


    public void onCreate() {
        ArrayList<PaperCameraBean> cameras = JsonUtils.prasePaperCameras(FileUtils.readStringFromAsset(mContext, "db.json"));
        ArrayList<GeoFenceInfo> infs = new ArrayList<>();

        for (PaperCameraBean paperCameraBean : cameras) {
            GeoFenceInfo geoFenceInfo = new GeoFenceInfo(mContext, new LatLng(paperCameraBean.getLatitude(), paperCameraBean.getLongtitude()), paperCameraBean.getId());
            infs.add(geoFenceInfo);
        }
        GeoFenceInfo geoFenceInfo = new GeoFenceInfo(mContext, new LatLng(40.09705f, 116.426019f), 100);
        infs.add(geoFenceInfo);
        //40.09705-116.426019

        mGeoFenceManager.addAllGeoFenceAler(infs);
    }

    public void onDestory() {
        mGeoFenceManager.removeAllGeoFenceAlert();
    }


}
