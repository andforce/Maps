package org.zarroboogs.maps.ui.navi;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.zarroboogs.maps.R;


public class NaviSettingsFragment extends PreferenceFragment {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navi_setting_activity_pref);

	}
}
