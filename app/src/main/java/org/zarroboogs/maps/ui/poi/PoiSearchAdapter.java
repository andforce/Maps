package org.zarroboogs.maps.ui.poi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.help.Tip;

import org.zarroboogs.maps.R;
import org.zarroboogs.maps.beans.PoiSearchTip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdiyuan on 15-7-24.
 */
public class PoiSearchAdapter extends BaseAdapter {

    private List<PoiSearchTip> mTips = new ArrayList<>();
    private LayoutInflater mInflater;

    public PoiSearchAdapter(Context context){
        if (mInflater == null){
            mInflater = LayoutInflater.from(context.getApplicationContext());
        }
    }
    public void addResultTips(List<PoiSearchTip> tips){
        mTips.clear();
        mTips.addAll(tips);

        notifyDataSetChanged();
    }

    public void clearTips(){
        mTips.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTips.size();
    }

    @Override
    public Object getItem(int i) {
        return mTips.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_layout,null);
            holder = new ViewHolder();
            holder.searchLocation = (ImageButton) convertView.findViewById(R.id.search_location);
            holder.searchNavi = (ImageButton) convertView.findViewById(R.id.search_navi);
            holder.searchNavi.setVisibility(View.GONE);

            holder.searchTitle = (TextView) convertView.findViewById(R.id.search_result_name);
            holder.searchSummary = (TextView) convertView.findViewById(R.id.search_result_summary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.searchTitle.setText(mTips.get(i).getName());
        holder.searchSummary.setText(mTips.get(i).getDistrict());

        return convertView;
    }


    class ViewHolder {
        ImageButton searchLocation;
        ImageButton searchNavi;

        TextView searchTitle;
        TextView searchSummary;
    }
}
