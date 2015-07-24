package org.zarroboogs.maps;

import android.view.View;

/**
 * Created by wangdiyuan on 15-7-24.
 */
public interface DrawerStateListener {
    public void onDrawerSlide(View drawerView, float slideOffset);

    public void onDrawerOpened(View drawerView);

    public void onDrawerClosed(View drawerView);

    public void onDrawerStateChanged(int newState);
}
