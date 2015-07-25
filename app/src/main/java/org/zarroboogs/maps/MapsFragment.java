package org.zarroboogs.maps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;

import org.zarroboogs.maps.poi.PoiKeywordSearchActivity;
import org.zarroboogs.maps.ui.ISearchMapsView;
import org.zarroboogs.maps.ui.MapsModule;
import org.zarroboogs.maps.ui.SearchMapsPresenter;

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
public class MapsFragment extends Fragment implements View.OnClickListener, DrawerStateListener, ISearchMapsView, OnKeyListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AMap aMap;
    private MapView mapView;
    private MapsModule mMapsModule;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MySensorEventListener mEventListener = new MySensorEventListener();
    private float mDevicesDirection = 0f;
    private ImageButton mCompass;

    private ImageButton mMyLocation;
    private ImageButton mDrawerSwitch;
    private ImageButton mSearchBtn;
    private AutoCompleteTextView mSearchEditText;

    private SearchMapsPresenter mSearchMapsPresenter;

    private OnFragmentInteractionListener mListener;

    private ListView mListView;
    private PoiSearchAdapter mPoiSearchAdapter;
    private SearchViewHelper mSearchViewHelper;
    
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

        mDrawerSwitch = (ImageButton) view.findViewById(R.id.left_drawer_switch);
        mDrawerSwitch.setOnClickListener(this);
        mSearchBtn = (ImageButton) view.findViewById(R.id.cancel_search);
        mSearchBtn.setOnClickListener(this);
        mSearchEditText = (AutoCompleteTextView) view.findViewById(R.id.poi_search_in_maps);
        mSearchEditText.setOnClickListener(this);

        mSearchViewHelper = new SearchViewHelper(view);

        mSearchEditText = (AutoCompleteTextView) view.findViewById(R.id.poi_search_in_maps);
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
                                if (rCode == 0) {// 正确返回
                                    List<String> listString = new ArrayList<String>();
                                    for (int i = 0; i < tipList.size(); i++) {
                                        listString.add(tipList.get(i).getName());
                                    }
                                    mPoiSearchAdapter.addResultTips(tipList);
                                }
                            }
                        });
                try {
                    inputTips.requestInputtips(newText, "北京");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

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
                mSearchEditText.setKeyListener(null);

                PoiSearchAdapter adapter = (PoiSearchAdapter) parent.getAdapter();
                Tip tip = ((Tip)adapter.getItem(position));
                mSearchEditText.setText(tip.getName());
                mSearchMapsPresenter.searchPoi(getActivity().getApplicationContext(), tip.getName(), tip.getDistrict());
                Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_LONG).show();

                mSearchEditText.setOnKeyListener(MapsFragment.this);
            }
        });
        
        
    }

    public AMap getGaoDeMap() {
        return aMap;
    }

    public ImageButton getMyLocationBtn(){
        return mMyLocation;
    }

    public float getDevicesDirection(){
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
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.left_drawer_switch){
            if (mSearchViewHelper.isInSearch()){
                mSearchMapsPresenter.exitSearch();
            } else{
                mSearchMapsPresenter.openDrawer();
            }

        } else if (id == R.id.cancel_search){
            if (mSearchViewHelper.isInSearch()){
                mSearchMapsPresenter.searchPoi(getActivity().getApplicationContext(), mSearchEditText.getText().toString(), "");
            } else{
                mSearchMapsPresenter.enterSearch();
            }

        } else if (id == R.id.poi_search_in_maps){
            mSearchMapsPresenter.enterSearch();
        }

        else {
            Intent intent = new Intent(getActivity(), PoiKeywordSearchActivity.class);
            startActivity(intent);
        }
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
        ((MapsMainActivity)getActivity()).openLeftDrawer();
    }

    @Override
    public void closeDrawer() {
        ((MapsMainActivity)getActivity()).closeLeftDrawer();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
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
        AutoCompleteTextView searchEditText;
        ListView listView;

        private boolean isInSearch = false;

        public boolean isInSearch(){
            return isInSearch;
        }
        public SearchViewHelper(View rootView){
            compassView = rootView.findViewById(R.id.ori_compass);
            myLocationView = rootView.findViewById(R.id.my_location_btn);
            searchMaskView = rootView.findViewById(R.id.search_mask);
            drawerSwitch = (ImageButton) rootView.findViewById(R.id.left_drawer_switch);
            searchEditText = (AutoCompleteTextView) rootView.findViewById(R.id.poi_search_in_maps);
            listView = (ListView) rootView.findViewById(R.id.search_result_list_view);

        }

        public void enterSearch(){
            isInSearch = true;
            listView.setVisibility(View.VISIBLE);
            compassView.setVisibility(View.GONE);
            myLocationView.setVisibility(View.GONE);
            searchMaskView.setVisibility(View.VISIBLE);
            drawerSwitch.setImageResource(R.drawable.ic_qu_appbar_back);
            searchEditText.setCursorVisible(true);
            showKeyboard(searchEditText);
        }

        public void exitSearch(){
            isInSearch = false;
            listView.setVisibility(View.GONE);
            compassView.setVisibility(View.VISIBLE);
            myLocationView.setVisibility(View.VISIBLE);
            searchMaskView.setVisibility(View.GONE);
            drawerSwitch.setImageResource(R.drawable.ic_qu_menu_grabber);
            searchEditText.setCursorVisible(false);
            searchEditText.setText("");
            hideKeyboard(searchEditText);
        }

        public void showSearchResult(){
            isInSearch = true;
            listView.setVisibility(View.GONE);
            compassView.setVisibility(View.GONE);
            myLocationView.setVisibility(View.GONE);
            searchMaskView.setVisibility(View.GONE);
            drawerSwitch.setImageResource(R.drawable.ic_qu_appbar_back);
            searchEditText.setCursorVisible(true);
        }
    }

    private void hideKeyboard(View view){
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view){
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    @Override
    public void enterSearch() {
        mSearchViewHelper.enterSearch();
    }

    @Override
    public void exitSearch() {
        mSearchViewHelper.exitSearch();

    }

    @Override
    public void showSearchResult(List<PoiItem> poiItems) {
        mSearchViewHelper.showSearchResult();
        aMap.clear();// 清理之前的图标
        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
    }

    @Override
    public void showSearchProgress() {

    }

    @Override
    public void hideSearchProgress() {

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


    class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float x = sensorEvent.values[SensorManager.DATA_X];
                if (Math.abs(x - mDevicesDirection) > 5) {
                    mCompass.setRotation(-x);
                    mDevicesDirection = x;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

}
