package org.zarroboogs.maps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

/**
 * AMapV2地图中介绍定位三种模式的使用，包括定位，追随，旋转
 */
public class LocationModeSourceActivity extends Activity implements OnCheckedChangeListener {
	private AMap aMap;
	private MapView mapView;

	private LocationManagerProxy mAMapLocationManager;
    private RadioGroup mGPSModeGroup;
	private MyLocationChangedListener myLocationChangedListener = new MyLocationChangedListener();
	private MyLocationSource source = new MyLocationSource();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationmodesource_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setLocationSource(source);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

		mGPSModeGroup=(RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setOnCheckedChangeListener(this);
	}

	class MyLocationChangedListener extends OnLocationChangedListener {

		@Override
		public void onGaodeLocationChanged(AMapLocation aMapLocation) {
			source.changeMyLocation(aMapLocation);
		}

	}

	class MyLocationSource implements LocationSource{

		private OnLocationChangedListener mListener;

		public void changeMyLocation(AMapLocation aLocation){
			mListener.onLocationChanged(aLocation);
		}
		@Override
		public void activate(OnLocationChangedListener onLocationChangedListener) {
			mListener = onLocationChangedListener;
			if (mAMapLocationManager == null) {
				mAMapLocationManager = LocationManagerProxy.getInstance(LocationModeSourceActivity.this);
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

		@Override
		public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(myLocationChangedListener);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
	switch(checkedId){
	case R.id.gps_locate_button:
		//设置定位的类型为定位模式 
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		break;
	case R.id.gps_follow_button:
		//设置定位的类型为 跟随模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		break;
	case R.id.gps_rotate_button:
		//设置定位的类型为根据地图面向方向旋转 
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
		break;
	}
		
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
//		deactivate();
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

}
