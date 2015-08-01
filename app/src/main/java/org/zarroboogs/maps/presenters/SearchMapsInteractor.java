package org.zarroboogs.maps.presenters;

import android.content.Context;

import com.amap.api.services.core.PoiItem;

import java.util.List;

/**
 * Created by andforce on 15/7/25.
 */
public interface SearchMapsInteractor {

    interface OnPoiSearchFinishedListener{
        public void onPoiSearchFinished(List<PoiItem> poiItems);
    }
    public void poiSearch(Context context ,String keyWord, String city, OnPoiSearchFinishedListener listener);


}
