package org.zarroboogs.maps.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.zarroboogs.maps.R;


public class SettingsFragment extends PreferenceFragment {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_activity_pref);
	}
}
