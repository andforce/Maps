package org.zarroboogs.maps.navi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;

import org.zarroboogs.maps.BaseActivity;
import org.zarroboogs.maps.MapsMainActivity;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.db.beans.CameraBean;
import org.zarroboogs.maps.module.TTSController;
import org.zarroboogs.maps.ui.MarkerInteractor;
import org.zarroboogs.maps.ui.MarkerInteractorImpl;
import org.zarroboogs.maps.utils.ToastUtil;
import org.zarroboogs.maps.utils.Utils;

import java.util.ArrayList;

/**
 * 实时导航界面
 * 
 */
public class NaviCustomActivity extends BaseActivity implements
		AMapNaviViewListener {

	private AMapNaviView mAmapAMapNaviView;
	// 导航可以设置的参数
	private boolean mDayNightFlag = Utils.DAY_MODE;// 默认为白天模式
	private boolean mDeviationFlag = Utils.YES_MODE;// 默认进行偏航重算
	private boolean mJamFlag = Utils.YES_MODE;// 默认进行拥堵重算
	private boolean mTrafficFlag = Utils.OPEN_MODE;// 默认进行交通播报
	private boolean mCameraFlag = Utils.OPEN_MODE;// 默认进行摄像头播报
	private boolean mScreenFlag = Utils.YES_MODE;// 默认是屏幕常亮
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
			@Override
			public void onReadCameras(ArrayList<CameraBean> cameraBeans) {
				ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
				for (CameraBean cameraBean : cameraBeans) {
					LatLng latLng = new LatLng(cameraBean.getLatitude(), cameraBean.getLongtitude());
					MarkerOptions mo = new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_camera_location));
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
		viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
		viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
		viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
		viewOptions.setTrafficInfoUpdateEnabled(mTrafficFlag);// 设置是否更新路况
		viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
		viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
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
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.THEME, mThemeStle);
		bundle.putBoolean(Utils.DAY_NIGHT_MODE, mDayNightFlag);
		bundle.putBoolean(Utils.DEVIATION, mDeviationFlag);
		bundle.putBoolean(Utils.JAM, mJamFlag);
		bundle.putBoolean(Utils.TRAFFIC, mTrafficFlag);
		bundle.putBoolean(Utils.CAMERA, mCameraFlag);
		bundle.putBoolean(Utils.SCREEN, mScreenFlag);
		Intent intent = new Intent(NaviCustomActivity.this,
				NaviSettingActivity.class);
		intent.putExtras(bundle);
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
	private void processBundle(Bundle bundle) {

		if (bundle != null) {
			mDayNightFlag = bundle.getBoolean(Utils.DAY_NIGHT_MODE, mDayNightFlag);
			mDeviationFlag = bundle.getBoolean(Utils.DEVIATION, mDeviationFlag);
			mJamFlag = bundle.getBoolean(Utils.JAM, mJamFlag);
			mTrafficFlag = bundle.getBoolean(Utils.TRAFFIC, mTrafficFlag);
			mCameraFlag = bundle.getBoolean(Utils.CAMERA, mCameraFlag);
			mScreenFlag = bundle.getBoolean(Utils.SCREEN, mScreenFlag);
			mThemeStle = bundle.getInt(Utils.THEME);

		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(NaviCustomActivity.this,
					MapsMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
		Bundle bundle = getIntent().getExtras();
		processBundle(bundle);
		setAmapNaviViewOptions();
		AMapNavi.getInstance(this).setAMapNaviListener(getAMapNaviListener());
		mAmapAMapNaviView.onResume();

		//语音播报开始
		TTSController.getInstance(NaviCustomActivity.this).startSpeaking();
		ToastUtil.show(NaviCustomActivity.this, "onInitNaviSuccess");

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
