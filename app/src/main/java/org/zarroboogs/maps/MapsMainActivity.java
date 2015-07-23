package org.zarroboogs.maps;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


/**
 * AMapV2地图中介绍定位三种模式的使用，包括定位，追随，旋转
 */
public class MapsMainActivity extends BaseActivity implements MapsFragment.OnFragmentInteractionListener,
        LeftDrawerFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_drawer);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.center_frame_layout, getMapsFragment(), MapsFragment.class.getName());
            ft.replace(R.id.left_drawer_layout, getLeftDrawerFragment(), LeftDrawerFragment.class.getName());
            ft.commit();


        }

    }


    private MapsFragment getMapsFragment() {
        MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MapsFragment.class.getName());
        if (mapsFragment == null) {
            mapsFragment = new MapsFragment();
        }
        return mapsFragment;
    }

    private LeftDrawerFragment getLeftDrawerFragment() {
        LeftDrawerFragment leftDrawerFragment = (LeftDrawerFragment) getSupportFragmentManager().findFragmentByTag(LeftDrawerFragment.class.getName());
        if (leftDrawerFragment == null) {
            leftDrawerFragment = new LeftDrawerFragment();
        }
        return leftDrawerFragment;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
