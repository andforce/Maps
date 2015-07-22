package org.zarroboogs.maps.ui;

/**
 * Created by andforce on 15/7/19.
 */
public interface MapsPresenter {

    public void loadDefaultCameraMarkers();
    public void enableDefaultGeoFences();

    public void disableDefaultGeoFences();

    public void changeMyLocationMode(int mode);

    public void stopFollowMode();


}
