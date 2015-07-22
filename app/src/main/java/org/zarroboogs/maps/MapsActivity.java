package org.zarroboogs.maps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

import org.zarroboogs.maps.poi.PoiKeywordSearchActivity;
import org.zarroboogs.maps.ui.MapsModule;


/**
 * AMapV2地图中介绍定位三种模式的使用，包括定位，追随，旋转
 */
public class MapsActivity extends BaseActivity implements View.OnClickListener {
    private AMap aMap;
    private MapView mapView;
    private MapsModule mMapsModule;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MySensorEventListener mEventListener = new MySensorEventListener();
    private float mDevicesDirection = 0f;
    private ImageButton mCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mCompass = (ImageButton) findViewById(R.id.ori_compass);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();


        mMapsModule = new MapsModule(this);
        mMapsModule.init();


        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        ImageButton searchBtn = (ImageButton) findViewById(R.id.poi_search_btn);
        searchBtn.setOnClickListener(this);


    }

    public AMap getGaoDeMap() {
        return aMap;
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float x = sensorEvent.values[SensorManager.DATA_X];
                if (Math.abs(x - mDevicesDirection) > 5) {
                    mCompass.setRotation(-x);
                    mDevicesDirection = x;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        mSensorManager.unregisterListener(mEventListener);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MapsActivity.this, PoiKeywordSearchActivity.class);
        startActivity(intent);
    }
}
