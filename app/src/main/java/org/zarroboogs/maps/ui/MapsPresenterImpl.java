package org.zarroboogs.maps.ui;

import com.amap.api.maps.model.MarkerOptions;

import org.zarroboogs.maps.ui.MarkerInteractor.OnMarkerCreatedListener;

import java.util.ArrayList;

/**
 * Created by andforce on 15/7/19.
 */
public class MapsPresenterImpl implements MapsPresenter, OnMarkerCreatedListener {

    private IGaoDeMapsView mGaodeMapsView;
    private MarkerInteractor mMapsInteractor;

    public MapsPresenterImpl(IGaoDeMapsView gaoDeMapsView) {
        this.mGaodeMapsView = gaoDeMapsView;
        this.mMapsInteractor = new MarkerInteractorImpl();
    }

    @Override
    public void loaddefaultCameras() {
        mMapsInteractor.createMarkers(this);
    }

    @Override
    public void onMarkerCreated(ArrayList<MarkerOptions> markerOptions) {
        mGaodeMapsView.addMarkers(markerOptions);
    }
}
