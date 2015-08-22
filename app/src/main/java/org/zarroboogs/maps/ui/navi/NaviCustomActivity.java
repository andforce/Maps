package org.zarroboogs.maps.ui.navi;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;

import org.zarroboogs.maps.beans.BJCamera;
import org.zarroboogs.maps.ui.BaseActivity;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.module.TTSController;
import org.zarroboogs.maps.presenters.MarkerInteractor;
import org.zarroboogs.maps.presenters.MarkerInteractorImpl;

import java.util.ArrayList;

/**
 * 实时导航界面
 * 
 */
public class NaviCustomActivity extends BaseActivity implements
		AMapNaviViewListener {

	private AMapNaviView mAmapAMapNaviView;
	// 导航界面风格
	private int mThemeStle;
	// 导航监听
	private AMapNaviListener mAmapNaviListener;
	private AMap mAmap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navicustom);

		//语音播报开始
		TTSController.getInstance(this).startSpeaking();
		// 实时导航方式进行导航
		AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
		
		initView(savedInstanceState);
		mAmap = mAmapAMapNaviView.getMap();

		MarkerInteractor markerInteractor = new MarkerInteractorImpl();
		markerInteractor.readCameras(new MarkerInteractor.OnReadCamerasListener() {
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_camera_location);
			@Override
			public void onReadCameras(ArrayList<BJCamera> cameraBeans) {
				ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
				for (BJCamera cameraBean : cameraBeans) {
					LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
					MarkerOptions mo = new MarkerOptions().position(latLng).draggable(true).icon(icon);
					markerOptionses.add(mo);
				}

				mAmap.addMarkers(markerOptionses, false);
			}
		});
	}

	private void initView(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.customnavimap);
		mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		setAmapNaviViewOptions();
	}

	/**
	 * 设置导航的参数
	 */
	private void setAmapNaviViewOptions() {
		if (mAmapAMapNaviView == null) {
			return;
		}
		AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
		viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
		viewOptions.setNaviNight(NaviSetting.getNaviNight());// 设置导航是否为黑夜模式
		viewOptions.setReCalculateRouteForYaw(NaviSetting.getReCalculateRouteForYaw());// 设置导偏航是否重算
		viewOptions.setReCalculateRouteForTrafficJam(NaviSetting.getReCalculateRouteForTrafficJam());// 设置交通拥挤是否重算
		viewOptions.setTrafficInfoUpdateEnabled(NaviSetting.getTrafficInfoUpdateEnabled());// 设置是否更新路况
		viewOptions.setCameraInfoUpdateEnabled(NaviSetting.getCameraInfoUpdateEnabled());// 设置摄像头播报
		viewOptions.setScreenAlwaysBright(NaviSetting.getScreenAlwaysBright());// 设置屏幕常亮情况
		viewOptions.setNaviViewTopic(mThemeStle);// 设置导航界面主题样式

		mAmapAMapNaviView.setViewOptions(viewOptions);

	}

	private AMapNaviListener getAMapNaviListener() {
		if (mAmapNaviListener == null) {
			mAmapNaviListener = TTSController.getInstance(getApplicationContext());
		}
		return mAmapNaviListener;
	}

	// -------处理
	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
		finish();
	}

	/**
	 * 点击设置按钮的事件
	 */
	@Override
	public void onNaviSetting() {
		Intent intent = new Intent(this,NaviSettingActivity.class);
		startActivity(intent);

	}

	@Override
	public void onNaviMapMode(int arg0) {

	}
	@Override
	public void onNaviTurnClick() {
		
		
	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}

	// ------------------------------生命周期方法---------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setAmapNaviViewOptions();

		AMapNavi.getInstance(this).setAMapNaviListener(getAMapNaviListener());
		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		mAmapAMapNaviView.onPause();
		super.onPause();
		AMapNavi.getInstance(this)
				.removeAMapNaviListener(getAMapNaviListener());

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		mAmapAMapNaviView.onDestroy();
	 	//页面结束时，停止语音播报
		TTSController.getInstance(this).stopSpeaking();
	}

	@Override
	public void onLockMap(boolean arg0) {
		  
		// TODO Auto-generated method stub  
		
	}

	

}
