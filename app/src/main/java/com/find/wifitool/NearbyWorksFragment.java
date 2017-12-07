package com.find.wifitool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.find.wifitool.database.MuseumWork;
import com.find.wifitool.internal.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyWorksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyWorksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyWorksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private Activity mContext;
    private FrameLayout workCanvas;
    private LinearLayout workLoader;
    private boolean loadingWorks = true;
    private int canvasLength = 1000;

    public NearbyWorksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyWorksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyWorksFragment newInstance(String param1, String param2) {
        NearbyWorksFragment fragment = new NearbyWorksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            updateNearbyWorks();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(mMessageReceiver,
                new IntentFilter("beacon-update"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_nearby_works, container, false);
        workCanvas = (FrameLayout)rootView.findViewById(R.id.workCanvas);
        workLoader = (LinearLayout)rootView.findViewById(R.id.workCanvasLoader);


        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateNearbyWorks() {

        HashMap beaconWorkMap = Constants.beaconWorkMap;

        List<MuseumWork> beaconWorks = new ArrayList<MuseumWork>(beaconWorkMap.values());
        Collections.sort(beaconWorks);

        if(loadingWorks) {
            removeLoader();
            this.loadingWorks = false;

            this.addImageViews(beaconWorks);

        }

        positionImageViews(beaconWorks);

        Log.d("NearbyWorks", "Updating...");
    }

    public void addImageViews(List<MuseumWork> beaconWorks) {
        for(MuseumWork aWork : beaconWorks) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(300, 300);
                    ImageView iv = new ImageView(this.mContext);

            Picasso.with(this.mContext).load(aWork.getImageURL()).into(iv);

            workCanvas.addView(iv, layoutParams);
            aWork.setImageView(iv);
        }
    }

    public void positionImageViews(List<MuseumWork> beaconWorks) {

        int workCanvasFrameHeight = workCanvas.getHeight();
        int workCanvasFrameWidth = workCanvas.getWidth();

        for(MuseumWork aWork : beaconWorks) {
            ImageView iv = aWork.getImageView();
            double distance = aWork.getDistance();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)iv.getLayoutParams();
            if(distance != 0.0) {
                // l t r b
                iv.setVisibility(View.VISIBLE);
                params.setMargins((int)distance*200, (int)distance*100, 0, 0);
                iv.setLayoutParams(params);
            } else {
                iv.setVisibility(View.INVISIBLE);
            }

        }


    }

    public void removeLoader() {
        this.workLoader.setVisibility(View.GONE);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
