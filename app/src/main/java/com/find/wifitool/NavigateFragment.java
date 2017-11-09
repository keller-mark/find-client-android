package com.find.wifitool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.find.wifitool.database.FloorLocation;
import com.find.wifitool.database.InternalDataBase;
import com.find.wifitool.internal.Constants;
import com.find.wifitool.internal.Utils;
import com.find.wifitool.wifi.WifiIntentReceiver;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigateFragment extends Fragment {
    private static final String TAG = NavigateFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //private variables
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strGroup;
    private FloorLocation currLocation = null;
    private int trackVal;

    private boolean spinnerLoaded = false;

    private boolean follow = true;

    private TextView currLocView, phillipsHeading;
    private Spinner changeFloorSpinner;
    private ImageView floorImageView;
    private ImageView geomarkerImageView;
    private Button showExhibitButton, showWorksButton;
    private ImageButton crosshairButton, closeExhibitButton;
    private FrameLayout exhibitFrameContainer;

    Handler handler = new Handler();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigateFragment newInstance(String param1, String param2) {
        NavigateFragment fragment = new NavigateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public NavigateFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Checking if the Location service is enabled in case of Android M or above users
        if (!Utils.isLocationAvailable(mContext)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Location service is not On. Users running Android M and above have to turn on location services for FIND to work properly");
            dialog.setPositiveButton("Enable Locations service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    logMeToast("Thank you!! Getting things in place.");
                }
            });
            dialog.show();
        }

        // Getting values from Shared prefs for Tracking
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Bind to views
        View rootView = inflater.inflate(R.layout.fragment_navigate, container, false);
        currLocView = (TextView)rootView.findViewById(R.id.labelLocationName);
        floorImageView = (ImageView)rootView.findViewById(R.id.floorImageView);
        geomarkerImageView = (ImageView)rootView.findViewById(R.id.geomarkerImageView);
        changeFloorSpinner = (Spinner)rootView.findViewById(R.id.changeFloorSpinner);
        showExhibitButton = (Button)rootView.findViewById(R.id.showExhibitButton);
        showWorksButton = (Button)rootView.findViewById(R.id.showWorksButton);
        crosshairButton = (ImageButton)rootView.findViewById(R.id.crosshairButton);
        closeExhibitButton = (ImageButton)rootView.findViewById(R.id.closeExhibitButton);
        exhibitFrameContainer = (FrameLayout)rootView.findViewById(R.id.exhibitFrameContainer);
        phillipsHeading = (TextView)rootView.findViewById(R.id.phillipsHeading);
        this.setupPhillipsHeading();

        this.populateFloorSpinner();

        // Initialize the "follow" button
        crosshairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableFollow();
            }
        });

        if(Constants.LEARN_MODE) {
            this.setupLearnFeatures();
        }

        // Start the WifiIntentReceiver code
        handler.post(runnableCode);

        // Listener to the broadcast message from WifiIntent
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter(Constants.TRACK_BCAST));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnableCode);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        mListener = null;
    }

    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    // Logging message in form of Toasts
    private void logMeToast(String message) {
        Log.d(TAG, message);
        toast(message);
    }

    // Timers to keep track of our Tracking period
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23) {
                if(Utils.isWiFiAvailable(mContext) && Utils.hasAnyLocationPermission(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.TRACK_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            }
            else if (Build.VERSION.SDK_INT < 23) {
                if(Utils.isWiFiAvailable(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            }
            else {
                return;
            }
            handler.postDelayed(runnableCode, trackVal * 1000);
        }
    };

    // Getting the CurrentLocation from the received braodcast
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String currLocationName = intent.getStringExtra("location");
            currLocView.setTextColor(getResources().getColor(R.color.currentLocationColor));

            InternalDataBase internalDataBase = new InternalDataBase(getActivity());
            FloorLocation savedLoc = internalDataBase.getLocation(currLocationName);

            if(savedLoc != null) {
                if(currLocation == null || !currLocation.equals(savedLoc)) {
                    currLocView.setText(savedLoc.getLocNamePretty());
                    currLocation = savedLoc;

                    if (follow) {
                        updateFloorImage(savedLoc.getFloorName());
                        moveGeoMarker(savedLoc);
                    }

                    // Vibrate for 500 milliseconds to alert about new location
                    Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            } else {
                currLocView.setText(currLocationName);
                currLocation = new FloorLocation();
                currLocation.setLocName(currLocationName);
            }

            enableShowExhibitButton();

        }
    };

    /* Floor plan UI helper functions */

    private void moveGeoMarker(FloorLocation floorLoc) {
        if(floorLoc != null) {
            this.geomarkerImageView.setVisibility(View.VISIBLE);
            double floorImageWidth = floorImageView.getWidth();
            double floorImageHeight = floorImageView.getHeight();

            double geomarkerImageWidth = geomarkerImageView.getWidth();
            double geomarkerImageHeight = geomarkerImageView.getHeight();

            this.geomarkerImageView.setTranslationX((float) (floorLoc.getLocX() * floorImageWidth - (geomarkerImageWidth / 2.0)));
            this.geomarkerImageView.setTranslationY((float) (floorLoc.getLocY() * floorImageHeight - geomarkerImageHeight));
        }
    }

    private void enableShowExhibitButton() {
        if(currLocation != null && currLocation.getLocExhibitID() > 0) {
            showExhibitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showExhibitInfo(currLocation.getLocExhibitID());
                }
            });

            this.showExhibitButton.setVisibility(View.VISIBLE);

            showWorksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWorksInfo(currLocation.getLocExhibitID());
                }
            });
            this.showWorksButton.setVisibility(View.VISIBLE);
        } else {
            this.showExhibitButton.setVisibility(View.GONE);
            this.showWorksButton.setVisibility(View.GONE);
        }

    }

    private void populateFloorSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.mContext, R.array.floors_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        changeFloorSpinner.setAdapter(adapter);


        changeFloorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String floorName = parent.getAdapter().getItem(position).toString();
                Log.d(TAG, floorName + " selected");
                updateFloorImage(floorName);

                if(spinnerLoaded) {
                    if(currLocation != null && !floorName.equals(currLocation.getFloorName())) {
                        disableFollow();
                    }
                } else {
                    spinnerLoaded = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateFloorImage(String floorName) {
        if(floorName != null) {
            int floorNameSpinnerPosition = ((ArrayAdapter<CharSequence>) changeFloorSpinner.getAdapter()).getPosition(floorName);
            changeFloorSpinner.setSelection(floorNameSpinnerPosition);

            Resources res = getResources();
            int resID = res.getIdentifier(floorName, "drawable", this.mContext.getPackageName());
            this.floorImageView.setImageResource(resID);

            if (currLocation != null && floorName.equals(currLocation.getFloorName())) {
                moveGeoMarker(currLocation);
            }
        }
    }

    private void setFollow(boolean follow) {
        this.follow = follow;
        this.crosshairButton.setVisibility((follow ? View.GONE : View.VISIBLE));
        if(follow) {
            if(currLocation != null) {
                this.updateFloorImage(currLocation.getFloorName());
            }
            this.moveGeoMarker(currLocation);
        } else {
            this.geomarkerImageView.setVisibility(View.GONE);
        }
    }

    private void enableFollow() {
        this.setFollow(true);
    }

    private void disableFollow() {
        this.setFollow(false);
    }

    private void showExhibitInfo(int exhibitID) {
        exhibitFrameContainer.setVisibility(View.VISIBLE);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        fm.beginTransaction();
        Fragment exhibitFragment = new ExhibitFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("exhibitID", exhibitID);
        exhibitFragment.setArguments(arguments);
        ft.add(R.id.frag_container, exhibitFragment);
        ft.commit();

        closeExhibitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(fm.findFragmentById(R.id.frag_container)).commit();
                exhibitFrameContainer.setVisibility(View.GONE);
            }
        });
    }

    private void showWorksInfo(int exhibitID) {
        exhibitFrameContainer.setVisibility(View.VISIBLE);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        fm.beginTransaction();
        Fragment nearbyWorksFragment = new NearbyWorksFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("exhibitID", exhibitID);
        nearbyWorksFragment.setArguments(arguments);
        ft.add(R.id.frag_container, nearbyWorksFragment);
        ft.commit();

        closeExhibitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(fm.findFragmentById(R.id.frag_container)).commit();
                exhibitFrameContainer.setVisibility(View.GONE);
            }
        });
    }

    private void setupLearnFeatures() {
        floorImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(currLocation != null) {
                    Log.d(TAG, "Touch coordinates : " + String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));

                    currLocation.setFloorName(changeFloorSpinner.getItemAtPosition(changeFloorSpinner.getSelectedItemPosition()).toString());

                    double floorImageWidth = floorImageView.getWidth();
                    double floorImageHeight = floorImageView.getHeight();

                    currLocation.setLocX(event.getX() / floorImageWidth);
                    currLocation.setLocY(event.getY() / floorImageHeight);
                    currLocation.setLocRatio(floorImageWidth / floorImageHeight);

                    InternalDataBase internalDataBase = new InternalDataBase(getActivity());
                    internalDataBase.addLocation(currLocation);

                    moveGeoMarker(currLocation);
                }
                return true;
            }
        });
    }

    private void setupPhillipsHeading() {
        String text = "" +
                "<font color=#75a0ae>P</font>" +
                "<font color=#d76c4a>h</font>" +
                "<font color=#7983b2>i</font>" +
                "<font color=#d9a94f>l</font>" +
                "<font color=#9dd8d3>l</font>" +
                "<font color=#b4bb66>i</font>" +
                "<font color=#e57524>p</font>" +
                "<font color=#9bc2be>s</font>" +
                " <font color=#999999>FIND</font>";
        phillipsHeading.setText(Html.fromHtml(text));
    }


}
