package org.zarroboogs.maps.presenters;

import android.content.Context;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import org.zarroboogs.maps.R;
import org.zarroboogs.maps.utils.ToastUtil;

import java.util.List;

/**
 * Created by andforce on 15/7/25.
 */
public class SearchMapsInteractorImpl implements SearchMapsInteractor, PoiSearch.OnPoiSearchListener {

    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private OnPoiSearchFinishedListener mOnPoiSearchFinishedListener;
    private Context mContext;

    @Override
    public void poiSearch(Context context ,String keyWord, String city, OnPoiSearchFinishedListener listener) {

        if (mOnPoiSearchFinishedListener == null){
            this.mOnPoiSearchFinishedListener = listener;
        }

        if (mContext == null){
            this.mContext = context;
        }

        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(1);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = result
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (mOnPoiSearchFinishedListener != null){
                        mOnPoiSearchFinishedListener.onPoiSearchFinished(poiItems);
                    }
                }
            } else {
                ToastUtil.show(mContext,
                        R.string.no_result);
            }
        } else if (rCode == 27) {
            ToastUtil.show(mContext,
                    R.string.error_network);
        } else if (rCode == 32) {
            ToastUtil.show(mContext, R.string.error_key);
        } else {
            ToastUtil.show(mContext, mContext.getString(R.string.error_other) + rCode);
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }
}
