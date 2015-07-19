package org.zarroboogs.maps.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by andforce on 15/7/18.
 */
public class CommUtils {

    private static Point sPoint = new Point();

    public static Point getScreenSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(sPoint);
        return sPoint;
    }

    private static Point sCompassPoint = new Point(0, 0);

    public static Point getCompassPoint(Context ctx) {
        if (sCompassPoint.x == 0 || sCompassPoint.y == 0) {
            Point screenSize = getScreenSize(ctx);
            sCompassPoint.set(screenSize.x / 2, 100);
        }
        return sCompassPoint;
    }
}
