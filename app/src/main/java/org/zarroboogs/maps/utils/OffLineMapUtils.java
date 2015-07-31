package org.zarroboogs.maps.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class OffLineMapUtils {
    /**
     * 获取map 缓存和读取目录
     */
    public static String getSdCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            if (true) {
                Context appContext = context.getApplicationContext();

                File amap = appContext.getExternalFilesDir("offlineMap");
                return amap.getAbsolutePath() + "/";
            } else {

                File fExternalStorageDirectory = Environment.getExternalStorageDirectory();
                File autonaviDir = new File(fExternalStorageDirectory, "amapsdk");
                boolean result = false;
                if (!autonaviDir.exists()) {
                    result = autonaviDir.mkdir();
                }
                File minimapDir = new File(autonaviDir, "offlineMap");
                if (!minimapDir.exists()) {
                    result = minimapDir.mkdir();
                }
                return minimapDir.toString() + "/";
            }

        } else {
            return "";
        }
    }
}
