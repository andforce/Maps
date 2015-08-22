package org.zarroboogs.maps.ui.maps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amap.api.maps.AMap;

import org.zarroboogs.maps.R;
import org.zarroboogs.maps.ui.offlinemaps.OfflineMapActivity;
import org.zarroboogs.maps.utils.SettingUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LeftDrawerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LeftDrawerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeftDrawerFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mOfflineBtn;
    private Button mSatelliteBtn;
    private Button mSettingBtn;
    private Button mCameraBtn;
    private Button mAboutBtn;

    private View mLeftTopView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeftDrawerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeftDrawerFragment newInstance(String param1, String param2) {
        LeftDrawerFragment fragment = new LeftDrawerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LeftDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOfflineBtn = (Button) view.findViewById(R.id.leftDrawerOfflineBtn);

        mOfflineBtn.setOnClickListener(this);

        mSatelliteBtn = (Button) view.findViewById(R.id.left_drawer_satellite);
        mSatelliteBtn.setOnClickListener(this);

        mSettingBtn = (Button) view.findViewById(R.id.left_drawer_setting);
        mSettingBtn.setOnClickListener(this);

        mCameraBtn = (Button) view.findViewById(R.id.left_drawer_camera);
        mCameraBtn.setOnClickListener(this);

        mAboutBtn = (Button)view.findViewById(R.id.leftDrawerAbout);
        mAboutBtn.setOnClickListener(this);

        mLeftTopView = view.findViewById(R.id.left_top_banner);

        initViewAfterViewCreated();
    }

    private void initViewAfterViewCreated() {
        Resources resources = getResources();

        if (SettingUtils.readCurrentMapsStyle() == AMap.MAP_TYPE_NORMAL) {
            mSatelliteBtn.setTextColor(resources.getColor(R.color.drawer_text_color_normal));
        } else {
            mSatelliteBtn.setTextColor(resources.getColor(R.color.drawer_text_color_blue));
        }

        if (SettingUtils.readCurrentCameraState() == SettingUtils.SWITCH_ON) {
            mCameraBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_blue));
        } else {
            mCameraBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_normal));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraBtn.setVisibility(SettingUtils.isEnableBeijingCamera() ? View.VISIBLE : View.GONE);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams layoutParams = mLeftTopView.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLeftTopView.setBackgroundResource(R.drawable.left_drawer_bg_land);
            layoutParams.height = (int) getResources().getDimension(R.dimen.left_drawer_top_view_height_land);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLeftTopView.setBackgroundResource(R.drawable.left_drawer_bg);
            layoutParams.height = (int) getResources().getDimension(R.dimen.left_drawer_top_view_height);
        }

        mLeftTopView.setLayoutParams(layoutParams);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
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
        if (id == R.id.left_drawer_satellite) {

            if (SettingUtils.readCurrentMapsStyle() == AMap.MAP_TYPE_SATELLITE) {
                mSatelliteBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_normal));
            } else {
                mSatelliteBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_blue));
            }
        } else if (id == R.id.left_drawer_camera) {
            if (SettingUtils.readCurrentCameraState() == SettingUtils.SWITCH_OFF) {
                mCameraBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_blue));
            } else {
                mCameraBtn.setTextColor(getResources().getColor(R.color.drawer_text_color_normal));
            }
        }
        onButtonPressed(id);


    }

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
        public void onFragmentInteraction(int id);
    }

}
