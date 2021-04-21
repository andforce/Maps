package org.zarroboogs.maps.ui.maps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import org.zarroboogs.maps.DrawerStateListener;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.ui.AboutActivity;
import org.zarroboogs.maps.ui.BaseActivity;
import org.zarroboogs.maps.ui.SettingActivity;
import org.zarroboogs.maps.ui.offlinemaps.OfflineMapActivity;


/**
 * AMapV2地图中介绍定位三种模式的使用，包括定位，追随，旋转
 */
public class MapsMainActivity extends BaseActivity implements MapsFragment.OnFragmentInteractionListener,
        LeftDrawerFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private DrawerStateListener mDrawerStateListener;
    private boolean mIsDrawerClicked = false;
    private int mDrawerClickedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_drawer);

        mDrawerStateListener = getMapsFragment();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.maps_drawer_layout);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (mDrawerStateListener != null) {
                    mDrawerStateListener.onDrawerSlide(drawerView, slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mDrawerStateListener != null) {
                    mDrawerStateListener.onDrawerOpened(drawerView);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mIsDrawerClicked){
                    if (mDrawerClickedId == R.id.leftDrawerOfflineBtn){
                        Intent intent = new Intent(MapsMainActivity.this, OfflineMapActivity.class);
                        startActivity(intent);

                    } else if (mDrawerClickedId == R.id.left_drawer_setting){
                        Intent intent = new Intent(MapsMainActivity.this, SettingActivity.class);
                        startActivity(intent);
                    } else if (mDrawerClickedId == R.id.leftDrawerAbout){
                        Intent intent = new Intent(MapsMainActivity.this, AboutActivity.class);
                        startActivity(intent);
                    } else{
                        getMapsFragment().onLeftDrawerViewClick(mDrawerClickedId);
                    }
                    mDrawerClickedId = -1;
                    mIsDrawerClicked = false;
                }else {
                    if (mDrawerStateListener != null) {
                        mDrawerStateListener.onDrawerClosed(drawerView);
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (mDrawerStateListener != null) {
                    mDrawerStateListener.onDrawerStateChanged(newState);
                }
            }
        });


        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.center_frame_layout, getMapsFragment(), MapsFragment.class.getName());
            ft.replace(R.id.left_drawer_layout, getLeftDrawerFragment(), LeftDrawerFragment.class.getName());
            ft.commit();
        }

    }

    public void openLeftDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeLeftDrawer() {
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
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
    public void onBackPressed() {
        if (getMapsFragment().isInSearch()) {
            getMapsFragment().exitSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(int id) {
        mIsDrawerClicked = true;
        mDrawerClickedId = id;
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
