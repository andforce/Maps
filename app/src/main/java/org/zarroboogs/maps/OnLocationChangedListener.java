package org.zarroboogs.maps;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by andforce on 15/8/1.
 */
public abstract class OnLocationChangedListener implements AMapLocationListener {

    public abstract void onGaodeLocationChanged(AMapLocation aMapLocation);

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        onGaodeLocationChanged(aMapLocation);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
