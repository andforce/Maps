package org.zarroboogs.maps.presenters.iviews;

import com.amap.api.services.core.PoiItem;

import java.util.List;

/**
 * Created by wangdiyuan on 15-7-24.
 */
public interface ISearchMapsView {
    public void openDrawer();
    public void closeDrawer();

    public void enterSearch();
    public void exitSearch();

    public void showSearchResult(List<PoiItem> poiItems);
    public void showSearchProgress();
    public void hideSearchProgress();

    public void showPoiFloatWindow(PoiItem poiItem);
}
