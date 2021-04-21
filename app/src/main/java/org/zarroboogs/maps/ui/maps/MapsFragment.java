package org.zarroboogs.maps.ui.maps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;

import org.zarroboogs.maps.DrawerStateListener;
import org.zarroboogs.maps.MapsApplication;
import org.zarroboogs.maps.beans.PoiSearchTip;
import org.zarroboogs.maps.ui.anim.AnimEndListener;
import org.zarroboogs.maps.ui.anim.ViewAnimUtils;
import org.zarroboogs.maps.ui.poi.PoiSearchAdapter;
import org.zarroboogs.maps.R;
import org.zarroboogs.maps.ui.navi.NaviRouteActivity;
import org.zarroboogs.maps.presenters.iviews.ISearchMapsView;
import org.zarroboogs.maps.ui.MapsModule;
import org.zarroboogs.maps.presenters.SearchMapsPresenter;
import org.zarroboogs.maps.utils.SettingUtils;
import org.zarroboogs.maps.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements View.OnClickListener, DrawerStateListener, ISearchMapsView, OnKeyListener {

    private static final boolean DEBUG = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean mLandscape = false;
    private float mOri = 0;

    private AMap aMap;
    private MapView mapView;
    private MapsModule mMapsModule;

    private SensorManager mSensorManager;
    private MySensorEventListener mEventListener = new MySensorEventListener();
    private float mDevicesDirection = 0f;
    private ImageButton mCompass;

    private ImageButton mMyLocation;
    private EditText mSearchEditText;

    private SearchMapsPresenter mSearchMapsPresenter;

    private OnFragmentInteractionListener mListener;

    private ListView mListView;
    private PoiSearchAdapter mPoiSearchAdapter;
    private SearchViewHelper mSearchViewHelper;


    // float window
    private RelativeLayout mSearchFloatWindow;
    private TextView mSearchPoiTitle;
    private TextView mSearchPoiSummary;
    private TextView mSearchPoiTel;
    private ImageButton mLineBtn;

    // progress dialog
    private ProgressDialog mSearchProgressDialog;



    private PoiItem mPoiItem;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        mSensorManager = (SensorManager) getActivity().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mSearchMapsPresenter = new SearchMapsPresenter(this);

        mPoiSearchAdapter = new PoiSearchAdapter(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCompass = (ImageButton) view.findViewById(R.id.ori_compass);
        mMyLocation = (ImageButton) view.findViewById(R.id.my_location_btn);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();


        mMapsModule = new MapsModule(this, aMap);
        mMapsModule.init();


        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        ImageButton mDrawerSwitch = (ImageButton) view.findViewById(R.id.left_drawer_switch);
        mDrawerSwitch.setOnClickListener(this);
        ImageButton mSearchBtn = (ImageButton) view.findViewById(R.id.cancel_search);
        mSearchBtn.setOnClickListener(this);
        mSearchEditText = (EditText) view.findViewById(R.id.poi_search_in_maps);
        mSearchEditText.setOnClickListener(this);

        mSearchViewHelper = new SearchViewHelper(view);

        mSearchEditText.setOnKeyListener(this);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString().trim();
                Inputtips inputTips = new Inputtips(getActivity().getApplicationContext(),
                        new Inputtips.InputtipsListener() {

                            @Override
                            public void onGetInputtips(List<Tip> tipList, int rCode) {

                                if (rCode == 0 && tipList != null) {// 正确返回
                                    List<PoiSearchTip> tips = new ArrayList<>();

                                    MapsApplication.getDaoSession().getPoiSearchTipDao().deleteAll();


                                    for (Tip tip : tipList) {

                                        PoiSearchTip mtip = new PoiSearchTip(tip.getName(), tip.getDistrict(), tip.getAdcode());

                                        MapsApplication.getDaoSession().getPoiSearchTipDao().insert(mtip);
                                        tips.add(mtip);
                                    }

                                    List<String> listString = new ArrayList<>();

                                    for (int i = 0; i < tipList.size(); i++) {
                                        listString.add(tipList.get(i).getName());
                                    }

                                    mPoiSearchAdapter.addResultTips(tips);

                                    if (mListView.getVisibility() == View.GONE){
                                        mListView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                try {
                    inputTips.requestInputtips(newText, "");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mListView = (ListView) view.findViewById(R.id.search_result_list_view);
        mListView.setAdapter(mPoiSearchAdapter);
        mListView.requestFocus();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(mSearchEditText);

                // stop follow mode
                mMapsModule.disableAutoLocation();

                PoiSearchAdapter adapter = (PoiSearchAdapter) parent.getAdapter();
                PoiSearchTip tip = (PoiSearchTip) adapter.getItem(position);
                mSearchEditText.setText(tip.getName());
                mSearchEditText.setSelection(mSearchEditText.getText().toString().length());

                Log.d("Search_OnItemClick ", "" + tip.toString());
                mSearchMapsPresenter.searchPoi(getActivity(), tip.getName(), tip.getAdcode());


            }
        });

        mSearchFloatWindow = (RelativeLayout) view.findViewById(R.id.search_float_rl);
        mSearchPoiTitle = (TextView) view.findViewById(R.id.search_result_title);
        mSearchPoiSummary = (TextView) view.findViewById(R.id.search_poi_summary);
        mLineBtn = (ImageButton) view.findViewById(R.id.maps_drive_line_btn);
        mSearchPoiTel = (TextView) view.findViewById(R.id.search_poi_tel);
    }

    public AMap getGaoDeMap() {
        return aMap;
    }

    public ImageButton getMyLocationBtn() {
        return mMyLocation;
    }

    public float getDevicesDirection() {
        return mDevicesDirection;
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor mPSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(mEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mEventListener,mPSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapsModule.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mSensorManager.unregisterListener(mEventListener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onLeftDrawerViewClick(int id){
        if (id == R.id.left_drawer_satellite){
            int currentStyle = SettingUtils.readCurrentMapsStyle();

            int newStyle = AMap.MAP_TYPE_NORMAL;

            if (currentStyle == AMap.MAP_TYPE_NORMAL){
                newStyle = AMap.MAP_TYPE_SATELLITE;
            } else{
                newStyle = AMap.MAP_TYPE_NORMAL;
            }

            SettingUtils.writeCurrentMapsStyle(newStyle);

            mMapsModule.changeMapStyle(newStyle);

        } else if (id == R.id.left_drawer_camera){
            int cameraSwitch = SettingUtils.readCurrentCameraState();
            if (cameraSwitch == SettingUtils.SWITCH_ON){
                SettingUtils.writeCurrentCameraState(SettingUtils.SWITCH_OFF);
                mMapsModule.removeCameras();
            } else {
                SettingUtils.writeCurrentCameraState(SettingUtils.SWITCH_ON);
                mMapsModule.loadCameras();
            }
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.left_drawer_switch) {
            if (mSearchViewHelper.isInSearch()) {
                mSearchMapsPresenter.exitSearch();
            } else {
                mSearchMapsPresenter.openDrawer();
            }

        } else if (id == R.id.cancel_search) {
            if (mSearchFloatWindow.getVisibility() == View.VISIBLE){
                mSearchMapsPresenter.exitSearch();
            } else{
                if (mSearchViewHelper.isInSearch()) {
                    String mSearchText = mSearchEditText.getText().toString();
                    if (TextUtils.isEmpty(mSearchText)){
                        Toast.makeText(getActivity().getApplicationContext(),R.string.search_no_text, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSearchMapsPresenter.searchPoi(getActivity().getApplicationContext(),mSearchText , "");
                } else {
                    mSearchMapsPresenter.enterSearch();
                }
            }


        } else if (id == R.id.poi_search_in_maps) {
            if (mSearchFloatWindow.getVisibility() == View.VISIBLE){
                mSearchMapsPresenter.enterSearch();
            } else{
                if (!mSearchViewHelper.isInSearch) {
                    mSearchMapsPresenter.enterSearch();
                }
            }

        }
    }

    public boolean isInSearch(){
        return mSearchViewHelper.isInSearch();
    }

    // DrawerLayout state
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void openDrawer() {
        ((MapsMainActivity) getActivity()).openLeftDrawer();
    }

    @Override
    public void closeDrawer() {
        ((MapsMainActivity) getActivity()).closeLeftDrawer();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            hideKeyboard(mSearchEditText);
            mSearchMapsPresenter.searchPoi(getActivity().getApplicationContext(), mSearchEditText.getText().toString(), "");
            return true;
        }

        return false;
    }

    class SearchViewHelper {
        View compassView;
        View myLocationView;
        View searchMaskView;
        ImageButton drawerSwitch;
        EditText searchEditText;
        ListView listView;
        private View floatWindow;
        private boolean isInSearch = false;

        public boolean isInSearch() {
            return isInSearch;
        }

        public SearchViewHelper(View rootView) {
            compassView = rootView.findViewById(R.id.ori_compass);
            myLocationView = rootView.findViewById(R.id.my_location_btn);
            searchMaskView = rootView.findViewById(R.id.search_mask);
            drawerSwitch = (ImageButton) rootView.findViewById(R.id.left_drawer_switch);
            searchEditText = (EditText) rootView.findViewById(R.id.poi_search_in_maps);
            listView = (ListView) rootView.findViewById(R.id.search_result_list_view);
            floatWindow =  rootView.findViewById(R.id.search_float_rl);
        }

        public void enterSearch() {
            isInSearch = true;
            floatWindow.setVisibility(View.GONE);

            compassView.setVisibility(View.GONE);
            myLocationView.setVisibility(View.GONE);

            if (listView.getAdapter().getCount() == 0){
                List<PoiSearchTip> tips = MapsApplication.getDaoSession().getPoiSearchTipDao().loadAll();
                if (tips.isEmpty()){
                    listView.setVisibility(View.GONE);
                } else{
                    mPoiSearchAdapter.addResultTips(tips);
                }
            }

            ViewAnimUtils.popupinWithInterpolator(searchMaskView, new AnimEndListener() {
                @Override
                public void onAnimEnd() {
                    searchMaskView.setVisibility(View.VISIBLE);
                }
            });

            if (listView.getAdapter().getCount() > 0){
                ViewAnimUtils.popupinWithInterpolator(listView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }


            drawerSwitch.setImageResource(R.drawable.ic_qu_appbar_back);
            searchEditText.setCursorVisible(true);

            showKeyboard(searchEditText);
        }

        public void exitSearch() {
            isInSearch = false;
            floatWindow.setVisibility(View.GONE);

            if (searchMaskView.getVisibility() == View.VISIBLE){
                ViewAnimUtils.popupoutWithInterpolator(searchMaskView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        searchMaskView.setVisibility(View.GONE);
                    }
                });
            }


            if (listView.getVisibility() == View.VISIBLE){
                ViewAnimUtils.popupoutWithInterpolator(listView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        listView.setVisibility(View.GONE);
                        compassView.setVisibility(View.VISIBLE);
                        myLocationView.setVisibility(View.VISIBLE);
                    }
                });
            } else{
                compassView.setVisibility(View.VISIBLE);
                myLocationView.setVisibility(View.VISIBLE);
            }


            drawerSwitch.setImageResource(R.drawable.ic_qu_menu_grabber);
            searchEditText.setText("");
            searchEditText.setCursorVisible(false);

            hideKeyboard(searchEditText);


        }

        public void showSuggestTips() {
            isInSearch = true;
            floatWindow.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            compassView.setVisibility(View.GONE);
            myLocationView.setVisibility(View.GONE);
            searchEditText.setCursorVisible(false);
            searchMaskView.setVisibility(View.GONE);
            drawerSwitch.setImageResource(R.drawable.ic_qu_appbar_back);

        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    @Override
    public void enterSearch() {
        mSearchViewHelper.enterSearch();
    }

    @Override
    public void exitSearch() {
        if (mPoiOverlay != null) {
            mPoiOverlay.removeFromMap();
            mPoiOverlay = null;
        }
        mSearchViewHelper.exitSearch();

    }

    private PoiOverlay mPoiOverlay;

    @Override
    public void showSearchResult(List<PoiItem> poiItems) {

        if (poiItems == null || poiItems.isEmpty()){
            ToastUtil.show(getActivity().getApplicationContext(), R.string.no_poi_search);
        } else {
            mSearchViewHelper.showSuggestTips();
            if (mPoiOverlay != null) {
                mPoiOverlay.removeFromMap();
                mPoiOverlay = null;
            }

            PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();

            mPoiOverlay = poiOverlay;


            mSearchMapsPresenter.showPoiFloatWindow(poiItems.get(0));

            mPoiItem = poiItems.get(0);
        }

    }

    @Override
    public void showSearchProgress() {
        if (mSearchProgressDialog == null){
            mSearchProgressDialog = new ProgressDialog(getActivity());
            mSearchProgressDialog.setMessage(getResources().getString(R.string.search_message));
        }
        mSearchProgressDialog.show();

    }

    @Override
    public void hideSearchProgress() {
        mSearchProgressDialog.hide();
    }

    @Override
    public void showPoiFloatWindow(PoiItem poiItem) {
        mSearchFloatWindow.setVisibility(View.VISIBLE);

        PoiItem item = poiItem;

        if (DEBUG){
            Log.d("showPoiFloatWindow     adCode                -"  , poiItem.getAdCode());
            Log.d("showPoiFloatWindow     adName                -"  , poiItem.getAdName());
            Log.d("showPoiFloatWindow     CityCOde              -"  , poiItem.getCityCode());
            Log.d("showPoiFloatWindow     cityName              -"  , poiItem.getCityName());
            Log.d("showPoiFloatWindow     direction             -"  , poiItem.getDirection());
            Log.d("showPoiFloatWindow     email                 -"  , poiItem.getEmail());
            Log.d("showPoiFloatWindow     poid                  -"  , poiItem.getPoiId());
            Log.d("showPoiFloatWindow     postCode              -"  , poiItem.getPostcode());
            Log.d("showPoiFloatWindow     provinceCode          -"  , poiItem.getProvinceCode());
            Log.d("showPoiFloatWindow     provincename          -"  , poiItem.getProvinceName());
            Log.d("showPoiFloatWindow     snippet               -"  , poiItem.getSnippet());
            Log.d("showPoiFloatWindow     tel                   -"  , poiItem.getTel());
            Log.d("showPoiFloatWindow     title                 -"  , poiItem.getTitle());
            Log.d("showPoiFloatWindow     typedes               -"  , poiItem.getTypeDes());
            Log.d("showPoiFloatWindow     website               -"  , poiItem.getWebsite());
            Log.d("showPoiFloatWindow     distance              -"  , poiItem.getDistance() + "");
        }


        
        mSearchPoiTitle.setText(poiItem.getTitle());
        String sum = "";
        if (!TextUtils.isEmpty(poiItem.getProvinceName())){
            sum += poiItem.getProvinceName();
        }

        if (!TextUtils.isEmpty(poiItem.getCityName())){
            sum += "-" + poiItem.getCityName();
        }

        if (!TextUtils.isEmpty(poiItem.getAdName())){
            sum += "-" + poiItem.getAdName();
        }

        if (!TextUtils.isEmpty(poiItem.getSnippet())){
            sum += "-" + poiItem.getSnippet();
        }

        mSearchPoiSummary.setText(sum);
        mSearchPoiTel.setText(poiItem.getTel());
        mLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapNavi aMapNavi = AMapNavi.getInstance(getActivity().getApplicationContext());
                ArrayList<NaviLatLng> start = new ArrayList<>();
                start.add(new NaviLatLng(mMapsModule.getMyLocation().getLatitude(), mMapsModule.getMyLocation().getLongitude()));

                ArrayList<NaviLatLng> end = new ArrayList<>();
                end.add(new NaviLatLng(mPoiItem.getLatLonPoint().getLatitude(), mPoiItem.getLatLonPoint().getLongitude()));

                aMapNavi.calculateDriveRoute(end, null, AMapNavi.DrivingDefault);

                Intent intent = new Intent(getActivity(), NaviRouteActivity.class);
                intent.putParcelableArrayListExtra(NaviRouteActivity.NAVI_ENDS, end);
                intent.putParcelableArrayListExtra(NaviRouteActivity.NAVI_START, start);
                getActivity().startActivity(intent);
            }
        });
//        mNaviBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NaviEmulatorActivity.class);
//                getActivity().startActivity(intent);
//            }
//        });
    }

    // DrawerLayout state

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLandscape = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float x = sensorEvent.values[SensorManager.DATA_X];

                float fixedX = x;
                if (mLandscape ){
                    if (mOri < 0) {
                        fixedX -= 90;
                        Log.d("onSensorChanged","Right" + x + "   fixedX :" + fixedX);
                    } else {
                        fixedX += 90;
                        Log.d("onSensorChanged","Left " + x + "   fixedX :" + fixedX);
                    }
                }

                if (Math.abs(fixedX - mDevicesDirection) > 2) {
                    if (SettingUtils.readCurrentMyLocationMode() == AMap.LOCATION_TYPE_MAP_ROTATE){
                        mCompass.setRotation(-fixedX);
                    } else{
                        mCompass.setRotation(0);
                    }

                    mDevicesDirection = fixedX;

                    mMapsModule.onOrientationChanged(mDevicesDirection);
                }
            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                float x = sensorEvent.values[SensorManager.DATA_X];
                float y = sensorEvent.values[SensorManager.DATA_Y];
                float z = sensorEvent.values[SensorManager.DATA_Z];
                mOri = x;
//                Log.d("PSensor-onSensorChanged","" + x + "   Ori :" + x);
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

}
