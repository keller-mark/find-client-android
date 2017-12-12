package com.find.wifitool.internal;

import com.find.wifitool.R;
import com.find.wifitool.database.MuseumExhibit;
import com.find.wifitool.database.MuseumWork;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshay on 30/12/16.
 */

public class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.find.wifitool";

    public static final boolean LEARN_MODE = true;

    // Shared prefs
    public static final String PREFS_NAME = PACKAGE_NAME + "com.find.wifitool.Prefs";
    public static final String USER_NAME = PACKAGE_NAME + "user";
    public static final String GROUP_NAME = PACKAGE_NAME + "group";
    public static final String SERVER_NAME = PACKAGE_NAME + "server";
    public static final String LOCATION_NAME = PACKAGE_NAME + "location";
    public static final String TRACK_INTERVAL = PACKAGE_NAME + "trackInterval";
    public static final String LEARN_INTERVAL = PACKAGE_NAME + "learnInterval";
    public static final String LEARN_PERIOD = PACKAGE_NAME + "learnPeriod";
    public static final String IS_FIRST_RUN = PACKAGE_NAME + "isFirstRun";

    //Default values
    public static final String DEFAULT_USERNAME = "user"; // not used
    public static final String DEFAULT_GROUP =  "pvc2017_avw12";
    public static final String DEFAULT_SERVER = "https://ml.internalpositioning.com/";
    public static final String DEFAULT_LOCATION_NAME = "location";
    public static final int DEFAULT_TRACKING_INTERVAL = 5;
    public static final int DEFAULT_LEARNING_INTERVAL = 5;
    public static final int DEFAULT_LEARNING_PERIOD = 3;

    // BRaodcast message tag
    public static final String TRACK_BCAST = "com.find.wifitool.track";

    public static final String TRACK_TAG = "track";
    public static final String LEARN_TAG = "learn";

    // Web URLs
    public static final String FIND_GITHUB_URL = "https://github.com/schollz/find";
    public static final String FIND_APP_URL = " https://github.com/uncleashi/find-client-android";
    public static final String FIND_WEB_URL = "https://www.internalpositioning.com/";
    public static final String FIND_ISSUES_URL = "https://github.com/schollz/find/issues";

    // Hard-coded exhibit details (for the sake of time this semester)
    public static final MuseumExhibit exhibit1 = new MuseumExhibit(R.string.exhibit1_title, R.string.exhibit1_subtitle, R.string.exhibit1_description, R.drawable.exhibit1_image);
    public static final MuseumExhibit exhibit2 = new MuseumExhibit(R.string.exhibit2_title, R.string.exhibit2_subtitle, R.string.exhibit2_description, R.drawable.exhibit2_image);
    public static final MuseumExhibit exhibit3 = new MuseumExhibit(R.string.exhibit3_title, R.string.exhibit3_subtitle, R.string.exhibit3_description, R.drawable.exhibit3_image);

    public static final MuseumWork work1 = new MuseumWork(1);
    public static final MuseumWork work2 = new MuseumWork(2);
    public static final MuseumWork work3 = new MuseumWork(3);
    public static final MuseumWork work4 = new MuseumWork(4);
    public static final MuseumWork work5 = new MuseumWork(5);

    public static final Map<Integer, MuseumExhibit> exhibitMap;
    public static final HashMap<Integer, MuseumWork> beaconWorkMap;

    static
    {
        work1.setPhillipsID("0002");
        work2.setPhillipsID("0003");
        work3.setPhillipsID("0004");
        work4.setPhillipsID("0005");
        work5.setPhillipsID("0007");

        beaconWorkMap = new HashMap<Integer, MuseumWork>();

        beaconWorkMap.put(1, work1);
        beaconWorkMap.put(2, work2);
        beaconWorkMap.put(3, work3);
        beaconWorkMap.put(4, work4);
        beaconWorkMap.put(5, work5);

        exhibitMap = new HashMap<Integer, MuseumExhibit>();
        exhibitMap.put(1, exhibit1);
        exhibitMap.put(2, exhibit2);
        exhibitMap.put(3, exhibit3);
    }


}
