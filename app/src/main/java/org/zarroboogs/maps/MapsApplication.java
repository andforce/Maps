package org.zarroboogs.maps;

import android.app.Application;
import android.content.Context;


import com.amap.api.navi.AMapNavi;

import org.zarroboogs.maps.beans.BJCamera;
import org.zarroboogs.maps.dao.DaoMaster;
import org.zarroboogs.maps.dao.DaoSession;
import org.zarroboogs.maps.module.TTSController;
import org.zarroboogs.maps.utils.Constants;
import org.zarroboogs.maps.utils.FileUtils;
import org.zarroboogs.maps.utils.JsonUtils;

import java.util.ArrayList;

public class MapsApplication extends Application {

    private static Context sContext;
    private static DaoMaster sDaoMaster;
    private static DaoSession sDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        TTSController ttsController = TTSController.getInstance(this.getApplicationContext());
        ttsController.init();

        AMapNavi navi = AMapNavi.getInstance(sContext);

        if (!FileUtils.readBooleanFromSharedPreference(Constants.PreferenceKeys.KEY_INIT, false)){
            ArrayList<BJCamera> cameraBeans = JsonUtils.prasePaperCameras(FileUtils.readStringFromAsset(MapsApplication.getAppContext(), "beijing_paper.json"));
            for (BJCamera camera: cameraBeans){
                getDaoSession().insert(camera);
            }
            FileUtils.writeBooleanToSharedPreference(Constants.PreferenceKeys.KEY_INIT,true);
        }

    }

    public static Context getAppContext() {
        return sContext;
    }

    public static DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(sContext, "maps.db", null);
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    public static DaoSession getDaoSession() {
        if (sDaoSession == null) {
            sDaoSession = getDaoMaster().newSession();
        }
        return sDaoSession;
    }

}