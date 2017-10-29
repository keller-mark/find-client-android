package com.find.wifitool;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.wifitool.database.MuseumExhibit;
import com.find.wifitool.internal.Constants;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExhibitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExhibitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExhibitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EXHIBIT_ID = "exhibitID";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int paramExhibitID;
    private String mParam2;


    TextView titleTextView, subtitleTextView, descriptionTextView;
    ImageView imageView;


    private OnFragmentInteractionListener mListener;

    public ExhibitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExhibitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExhibitFragment newInstance(String param1, String param2) {
        ExhibitFragment fragment = new ExhibitFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EXHIBIT_ID, 1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramExhibitID = getArguments().getInt(ARG_EXHIBIT_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_exhibit, container, false);
        titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        subtitleTextView = (TextView)rootView.findViewById(R.id.subtitleTextView);
        imageView = (ImageView)rootView.findViewById(R.id.imageView);
        descriptionTextView = (TextView)rootView.findViewById(R.id.descriptionTextView);


        MuseumExhibit exhibit = Constants.exhibitMap.get(this.getArguments().getInt(ARG_EXHIBIT_ID));
        this.updateExhibit(exhibit);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exhibit, container, false);
    }

    public void updateExhibit(MuseumExhibit exhibit) {
        if(exhibit != null) {
            titleTextView.setText(exhibit.getTitleResourceID());
            subtitleTextView.setText(exhibit.getSubtitleResourceID());
            descriptionTextView.setText(exhibit.getDescriptionResourceID());
            imageView.setImageResource(exhibit.getImageResourceID());
        } else {
            Log.d(TAG, "NULL EXHIBIT, CANNOT UPDATE");
        }
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
