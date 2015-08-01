package org.zarroboogs.maps.ui.navi;

import com.amap.api.navi.AMapNaviViewOptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import org.zarroboogs.maps.ui.BaseActivity;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.utils.Utils;

/**
 * 导航设置界面
 *
 */
public class NaviSettingActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	// ----------------View

	private ImageView mBackView;//返回按钮
	private RadioGroup mDayNightGroup;//黑夜模式白天模式
	private RadioGroup mDeviationGroup;//偏航重算
	private RadioGroup mJamGroup;//拥堵重算
	private RadioGroup mTrafficGroup;//交通播报
	private RadioGroup mCameraGroup;//摄像头播报
	private RadioGroup mScreenGroup;//屏幕常亮

	private boolean mDayNightFlag = Utils.DAY_MODE;
	private boolean mDeviationFlag = Utils.YES_MODE;
	private boolean mJamFlag = Utils.YES_MODE;
	private boolean mTrafficFlag = Utils.OPEN_MODE;
	private boolean mCameraFlag = Utils.OPEN_MODE;
	private boolean mScreenFlag = Utils.YES_MODE;
	private int mThemeStyle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navisetting);
		Bundle bundle=getIntent().getExtras();
		processBundle(bundle);
		initView();
		initListener();
	}


	/**
	 * 初始化控件
	 */
	private void initView() {
		mBackView = (ImageView) findViewById(R.id.setting_back_image);
		mDayNightGroup = (RadioGroup) findViewById(R.id.day_night_group);
		mDeviationGroup = (RadioGroup) findViewById(R.id.deviation_group);
		mJamGroup = (RadioGroup) findViewById(R.id.jam_group);
		mTrafficGroup = (RadioGroup) findViewById(R.id.traffic_group);
		mCameraGroup = (RadioGroup) findViewById(R.id.camera_group);
		mScreenGroup = (RadioGroup) findViewById(R.id.screen_group);

	}

	/**
	 * 初始化监听事件
	 */
	private void initListener() {
		mBackView.setOnClickListener(this);
		mDayNightGroup.setOnCheckedChangeListener(this);
		mDeviationGroup.setOnCheckedChangeListener(this);
		mJamGroup.setOnCheckedChangeListener(this);
		mTrafficGroup.setOnCheckedChangeListener(this);
		mCameraGroup.setOnCheckedChangeListener(this);
		mScreenGroup.setOnCheckedChangeListener(this);

	}

	/**
	 * 根据导航界面传过来的数据设置当前界面的显示状态
	 */
	private void setViewContent() {
		if (mDayNightGroup == null) {
			return;
		}
		if (mDayNightFlag) {
			mDayNightGroup.check(R.id.nightradio);
		} else {
			mDayNightGroup.check(R.id.dayratio);
		}
		if (mDeviationFlag) {
			mDeviationGroup.check(R.id.deviationyesradio);
		} else {
			mDeviationGroup.check(R.id.deviationnoradio);
		}

		if (mJamFlag) {
			mJamGroup.check(R.id.jam_yes_radio);
		} else {
			mJamGroup.check(R.id.jam_no_radio);
		}

		if (mTrafficFlag) {
			mTrafficGroup.check(R.id.trafficyesradio);
		} else {
			mTrafficGroup.check(R.id.trafficnoradio);
		}

		if (mCameraFlag) {
			mCameraGroup.check(R.id.camerayesradio);
		} else {
			mCameraGroup.check(R.id.cameranoradio);
		}

		if (mScreenFlag) {
			mScreenGroup.check(R.id.screenonradio);
		} else {
			mScreenGroup.check(R.id.screenoffradio);
		}
	}

	/**
	 * 处理具体的bundle
	 * @param bundle
	 */
	private void processBundle(Bundle bundle) {
		if (bundle != null) {
			mThemeStyle = bundle.getInt(Utils.THEME,
					AMapNaviViewOptions.DEFAULT_COLOR_TOPIC);
			mDayNightFlag = bundle.getBoolean(Utils.DAY_NIGHT_MODE);
			mDeviationFlag = bundle.getBoolean(Utils.DEVIATION);
			mJamFlag = bundle.getBoolean(Utils.JAM);
			mTrafficFlag = bundle.getBoolean(Utils.TRAFFIC);
			mCameraFlag = bundle.getBoolean(Utils.CAMERA);
			mScreenFlag = bundle.getBoolean(Utils.SCREEN);

		}
	}

	/**
	 * 根据当前界面的设置设置，构建bundle
	 * @return
	 */
	private Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(Utils.DAY_NIGHT_MODE, mDayNightFlag);
		bundle.putBoolean(Utils.DEVIATION, mDeviationFlag);
		bundle.putBoolean(Utils.JAM, mJamFlag);
		bundle.putBoolean(Utils.TRAFFIC, mTrafficFlag);
		bundle.putBoolean(Utils.CAMERA, mCameraFlag);
		bundle.putBoolean(Utils.SCREEN, mScreenFlag);
		bundle.putInt(Utils.THEME, mThemeStyle);
		return bundle;
	}

	// 事件处理方法
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_back_image:

			Intent intent = new Intent(NaviSettingActivity.this,
					NaviCustomActivity.class);
			intent.putExtras(getBundle());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
			break;
		}

	}

	
/**
 * 返回键监听
 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(NaviSettingActivity.this,
					NaviCustomActivity.class);
			intent.putExtras(getBundle());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();

		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------生命周期重写方法---------------------------

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setViewContent();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		// 昼夜模式
		case R.id.dayratio:
			mDayNightFlag = Utils.DAY_MODE;
			break;
		case R.id.nightradio:
			mDayNightFlag = Utils.NIGHT_MODE;
			break;
		// 偏航重算
		case R.id.deviationyesradio:
			mDeviationFlag = Utils.YES_MODE;
			break;
		case R.id.deviationnoradio:
			mDeviationFlag = Utils.NO_MODE;
			break;
		// 拥堵重算
		case R.id.jam_yes_radio:
			mJamFlag = Utils.YES_MODE;
			break;
		case R.id.jam_no_radio:
			mJamFlag = Utils.NO_MODE;
			break;
		// 交通播报
		case R.id.trafficyesradio:
			mTrafficFlag = Utils.OPEN_MODE;
			break;
		case R.id.trafficnoradio:
			mTrafficFlag = Utils.CLOSE_MODE;
			break;
		// 摄像头播报
		case R.id.camerayesradio:
			mCameraFlag = Utils.OPEN_MODE;
			break;
		case R.id.cameranoradio:
			mCameraFlag = Utils.CLOSE_MODE;
			break;
			// 屏幕常亮
		case R.id.screenonradio:
			mScreenFlag = Utils.YES_MODE;
			break;
		case R.id.screenoffradio:
			mScreenFlag = Utils.NO_MODE;
			break;
		}

	}
}
