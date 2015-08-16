package org.zarroboogs.maps.ui.navi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNavi;

import org.zarroboogs.maps.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdiyuan on 15-7-24.
 */
public class RoutSettingAdapter extends BaseAdapter {

    private List<RoutType> mRoutTypes = new ArrayList<>();
    private LayoutInflater mInflater;

    public RoutSettingAdapter(Context context) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context.getApplicationContext());
        }

        mRoutTypes.add(new RoutType("速度优先", AMapNavi.DrivingDefault));
        mRoutTypes.add(new RoutType("避免收费", AMapNavi.DrivingSaveMoney));
        mRoutTypes.add(new RoutType("距离最短", AMapNavi.DrivingShortDistance));
        mRoutTypes.add(new RoutType("不走高速", AMapNavi.DrivingNoExpressways));
        mRoutTypes.add(new RoutType("时间最短且躲避拥堵", AMapNavi.DrivingFastestTime));
        mRoutTypes.add(new RoutType("躲避收费且躲避拥堵", AMapNavi.DrivingAvoidCongestion));


    }


    @Override
    public int getCount() {
        return mRoutTypes.size();
    }

    @Override
    public Object getItem(int i) {
        return mRoutTypes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rout_setting_listview_item_layout, null);
            holder = new ViewHolder();
            holder.searchTitle = (TextView) convertView.findViewById(R.id.rout_setting);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.searchTitle.setText(mRoutTypes.get(i).getName());
        return convertView;
    }


    class ViewHolder {
        TextView searchTitle;
    }
}
