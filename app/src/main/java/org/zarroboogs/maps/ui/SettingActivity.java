package org.zarroboogs.maps.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import org.zarroboogs.maps.R;


public class SettingActivity extends BaseActivity{


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		if (savedInstanceState == null){
			getFragmentManager().beginTransaction().replace(R.id.setting_fragment_frame, new SettingsFragment()).commit();
		}

        ImageButton back = (ImageButton) findViewById(R.id.setting_back_image);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

}
