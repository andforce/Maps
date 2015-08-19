package org.zarroboogs.maps.beans;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.amap.api.maps.model.LatLng;


/**
 * Created by wangdiyuan on 15-7-17.
 */
public class GeoFenceInfo {
    private LatLng latLng;
    private long mId = 0;

    public static final String GEOFENCE_BROADCAST_ACTION = "org.zarroboogs.maps.geofence_broadcast";

    private IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    private PendingIntent mPendingIntent;

    public GeoFenceInfo(Context context, LatLng ll, long id) {
        this.mId = id;
        this.latLng = ll;
        filter.addAction(GEOFENCE_BROADCAST_ACTION + mId);

        Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION + mId);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public String getAction() {
        return GEOFENCE_BROADCAST_ACTION + mId;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public PendingIntent getPendingIntent() {
        return mPendingIntent;
    }

    public IntentFilter getFilter() {
        return filter;
    }
}
