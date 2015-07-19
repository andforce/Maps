package org.zarroboogs.maps;

import android.app.Application;
import android.content.Context;


import org.zarroboogs.maps.module.TTSController;

public class MapsApplication extends Application {

	private static Context sCntext;

	@Override
	public void onCreate() {
		super.onCreate();
		sCntext = this;
		TTSController ttsController = TTSController.getInstance(this.getApplicationContext());
		ttsController.init();
	}

	public static Context getAppContext(){
		return sCntext;
	}

}