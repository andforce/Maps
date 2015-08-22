package org.zarroboogs.maps.ui.navi;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.zarroboogs.maps.ui.BaseActivity;
import org.zarroboogs.maps.R;

/**
 * 导航设置界面
 *
 */
public class NaviSettingActivity extends BaseActivity{

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		setContentView(R.layout.activity_navisetting);

		if (savedInstanceState == null){
			getFragmentManager().beginTransaction().replace(R.id.navi_setting_frame, new NaviSettingsFragment()).commit();
		}

        ImageView mBackView = (ImageView) findViewById(R.id.setting_back_image);
        mBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}


}
