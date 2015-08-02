package org.zarroboogs.maps.ui.offlinemaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;

import org.zarroboogs.maps.ui.BaseActivity;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.utils.OffLineMapUtils;

/**
 * AMapV2地图中简单介绍离线地图下载
 */
public class OfflineMapActivity extends BaseActivity implements
        OfflineMapDownloadListener {
    private OfflineMapManager amapManager = null;// 离线地图下载控制器
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
    private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// 保存二级目录的市
    private int groupPosition = -1;// 记录一级目录的position
    private int childPosition = -1;// 记录二级目录的position
    private int completeCode;// 记录下载比例
    private boolean isStart = false;// 判断是否开始下载,true表示开始下载，false表示下载失败
    private boolean[] isOpen;// 记录一级目录是否打开
    private ImageButton mOffLineMapsBack;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
        // Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
        setContentView(R.layout.offlinemap_activity);
        init();

        mOffLineMapsBack = (ImageButton) findViewById(R.id.offline_back);

        mOffLineMapsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private MapView mapView;

    /**
     * 初始化UI布局文件
     */
    private void init() {
        // 此版本限制，使用离线地图，请初始化一个MapView
        mapView = new MapView(this);
        amapManager = new OfflineMapManager(this, this);
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.list);
        expandableListView.setGroupIndicator(null);
        amapManager.getItemByProvinceName("安徽省").getCityList();
        provinceList = amapManager.getOfflineMapProvinceList();
        List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// 以省格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        List<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图
        for (int i = 0; i < provinceList.size(); i++) {
            OfflineMapProvince offlineMapProvince = provinceList.get(i);
            List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
            OfflineMapCity aMapCity = getCicy(offlineMapProvince);
            if (offlineMapProvince.getCityList().size() != 1) {
                city.add(aMapCity);
                city.addAll(offlineMapProvince.getCityList());
            } else {
                cityList.add(aMapCity);
                bigCityList.add(offlineMapProvince);
            }
            cityMap.put(i + 3, city);
        }
        OfflineMapProvince title = new OfflineMapProvince();

        title.setProvinceName("概要图");
        provinceList.add(0, title);
        title = new OfflineMapProvince();
        title.setProvinceName("直辖市");
        provinceList.add(1, title);
        title = new OfflineMapProvince();
        title.setProvinceName("港澳");
        provinceList.add(2, title);
        provinceList.removeAll(bigCityList);

        for (OfflineMapProvince aMapProvince : bigCityList) {
            if (aMapProvince.getProvinceName().contains("香港")
                    || aMapProvince.getProvinceName().contains("澳门")) {
                gangaoList.add(getCicy(aMapProvince));
            } else if (aMapProvince.getProvinceName().contains("全国概要图")) {
                gaiyaotuList.add(getCicy(aMapProvince));
            }
        }
        try {
            cityList.remove(4);// 从List集合体中删除香港
            cityList.remove(4);// 从List集合体中删除澳门
            cityList.remove(4);// 从List集合体中删除澳门
        } catch (Throwable e) {
            e.printStackTrace();
        }
        cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
        cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
        cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳
        isOpen = new boolean[provinceList.size()];
        // 为列表绑定数据源
        expandableListView.setAdapter(adapter);
        expandableListView
                .setOnGroupCollapseListener(new OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        isOpen[groupPosition] = false;
                    }
                });

        expandableListView
                .setOnGroupExpandListener(new OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        isOpen[groupPosition] = true;
                    }
                });
        // 设置二级item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                try {
                    // 下载全国概要图、直辖市、港澳离线地图数据
                    if (groupPosition == 0 || groupPosition == 1
                            || groupPosition == 2) {
                        isStart = amapManager.downloadByProvinceName(cityMap
                                .get(groupPosition).get(childPosition)
                                .getCity());
                    }
                    // 下载各省的离线地图数据
                    else {
                        // 下载各省列表中的省份离线地图数据
                        if (childPosition == 0) {
                            isStart = amapManager
                                    .downloadByProvinceName(provinceList.get(
                                            groupPosition).getProvinceName());
                        }
                        // 下载各省列表中的城市离线地图数据
                        else if (childPosition > 0) {
                            isStart = amapManager.downloadByCityName(cityMap
                                    .get(groupPosition).get(childPosition)
                                    .getCity());
                        }
                    }
                } catch (AMapException e) {
                    e.printStackTrace();
                    Log.e("离线地图下载", "离线地图下载抛出异常" + e.getErrorMessage());
                }
                Log.i("yiyi.qi", groupPosition + " " + childPosition);
                // 保存当前正在正在下载省份或者城市的position位置
                if (isStart) {
                    OfflineMapActivity.this.groupPosition = groupPosition;
                    OfflineMapActivity.this.childPosition = childPosition;
                }
                return false;
            }
        });
    }

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }

    final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

        @Override
        public int getGroupCount() {
            return provinceList.size();
        }

        /**
         * 获取一级标签内容
         */
        @Override
        public Object getGroup(int groupPosition) {
            return provinceList.get(groupPosition).getProvinceName();
        }

        /**
         * 获取一级标签的ID
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * 获取一级标签下二级标签的总数
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return cityMap.get(groupPosition).size();
        }

        /**
         * 获取一级标签下二级标签的内容
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return cityMap.get(groupPosition).get(childPosition).getCity();
        }

        /**
         * 获取二级标签的ID
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 指定位置相应的组视图
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * 对一级标签进行设置
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView group_text;
            ImageView group_image;
            if (convertView == null) {
                convertView = (RelativeLayout) RelativeLayout.inflate(
                        getBaseContext(), R.layout.offlinemap_group, null);
            }
            group_text = (TextView) convertView.findViewById(R.id.group_text);
            group_image = (ImageView) convertView
                    .findViewById(R.id.group_image);
            group_text.setText(provinceList.get(groupPosition)
                    .getProvinceName());
            if (isOpen[groupPosition]) {
                group_image.setImageDrawable(getResources().getDrawable(
                        R.drawable.offline_maps_down_arrow));
            } else {
                group_image.setImageDrawable(getResources().getDrawable(
                        R.drawable.offline_maps_right_arrow));
            }
            return convertView;
        }

        /**
         * 对一级标签下的二级标签进行设置
         */
        @Override
        public View getChildView(final int groupPosition,
                                 final int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            if (convertView == null) {
                convertView = (RelativeLayout) RelativeLayout.inflate(
                        getBaseContext(), R.layout.offlinemap_child, null);
            }
            ViewHolder holder = new ViewHolder(convertView);
            holder.cityName.setText(cityMap.get(groupPosition)
                    .get(childPosition).getCity());
            holder.citySize.setText((cityMap.get(groupPosition).get(
                    childPosition).getSize())
                    / (1024 * 1024f) + "MB");

            if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.SUCCESS) {
                holder.cityDown.setText("安装完成");
            } else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
                if (groupPosition == OfflineMapActivity.this.groupPosition
                        && childPosition == OfflineMapActivity.this.childPosition) {
                    holder.cityDown.setText("正在下载" + completeCode + "%");
                } else {
                    if (cityMap.get(groupPosition).get(childPosition).getcompleteCode() > 0) {
                        holder.cityDown.setText("下载暂停" + cityMap.get(groupPosition).get(childPosition).getcompleteCode() + "%");
                    } else {
                        holder.cityDown.setText("");
                    }


                }
            } else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.UNZIP) {
                holder.cityDown.setText("正在解压" + completeCode + "%");
            } else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
                holder.cityDown.setText("下载");
            }
            return convertView;
        }

        class ViewHolder {
            TextView cityName;
            TextView citySize;
            TextView cityDown;

            public ViewHolder(View view) {
                cityName = (TextView) view.findViewById(R.id.city_name);
                citySize = (TextView) view.findViewById(R.id.city_size);
                cityDown = (TextView) view.findViewById(R.id.city_down);
            }
        }

        /**
         * 当选择子节点的时候，调用该方法
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    };

    /**
     * 离线地图下载回调方法
     */
    @Override
    public void onDownload(int status, int completeCode, String downName) {

        switch (status) {
            case OfflineMapStatus.SUCCESS:
                changeOfflineMapTitle(OfflineMapStatus.SUCCESS);
                break;
            case OfflineMapStatus.LOADING:
                OfflineMapActivity.this.completeCode = completeCode;
                break;
            case OfflineMapStatus.UNZIP:
                OfflineMapActivity.this.completeCode = completeCode;
                changeOfflineMapTitle(OfflineMapStatus.UNZIP);
                break;
            case OfflineMapStatus.WAITING:
                break;
            case OfflineMapStatus.PAUSE:
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                break;
            default:
                break;
        }
        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();

    }

    /**
     * 更改离线地图下载状态文字
     */
    private void changeOfflineMapTitle(int status) {
        if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) {
            cityMap.get(groupPosition).get(childPosition).setState(status);
        } else {
            if (childPosition == 0) {
                for (int i = 0; i < cityMap.get(groupPosition).size(); i++) {
                    cityMap.get(groupPosition).get(i).setState(status);//
                }
            } else {
                cityMap.get(groupPosition).get(childPosition).setState(status);
            }
        }


    }

    /**
     * 获取map 缓存和读取目录
     */
    private String getSdCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            java.io.File fExternalStorageDirectory = Environment
                    .getExternalStorageDirectory();
            java.io.File autonaviDir = new java.io.File(
                    fExternalStorageDirectory, "amapsdk");
            boolean result = false;
            if (!autonaviDir.exists()) {
                result = autonaviDir.mkdir();
            }
            java.io.File minimapDir = new java.io.File(autonaviDir,
                    "offlineMap");
            if (!minimapDir.exists()) {
                result = minimapDir.mkdir();
            }
            return minimapDir.toString() + "/";
        } else {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }
}
