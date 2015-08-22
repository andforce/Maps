package org.zarroboogs.maps.ui;

import android.os.Bundle;

import org.zarroboogs.maps.R;


public class SettingActivity extends BaseActivity{


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		if (savedInstanceState == null){
			getFragmentManager().beginTransaction().replace(R.id.setting_fragment_frame, new SettingsFragment()).commit();
		}
	}


}
