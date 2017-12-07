package com.find.wifitool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.find.wifitool.database.MuseumWork;
import com.find.wifitool.internal.Constants;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.EddystoneUid;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1234;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        if(!Constants.LEARN_MODE) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Check for Android M runtime permissions
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        // Calling function to set some default values if its our first run
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        setDefaultPrefs();



        // Subscribe for all Eddystone UIDs whose first 10 bytes (the "namespace")
        // match MY_EDDYSTONE_UID_NAMESPACE.

        // Note that the Eddystone UID namespace is separate from the namespace
        // field of a Nearby Message.
        MessageFilter messageFilter = new MessageFilter.Builder()
                .includeEddystoneUids("00000000000000000001", null /* any instance */)
                .build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(messageFilter)
                .build();

        MessageListener messageListener = new MessageListener() {

            /**
             * Called when a message is discovered nearby.
             */
            @Override
            public void onFound(final Message message) { }

            /**
             * Called when the Bluetooth Low Energy (BLE) signal associated with a message changes.
             *
             * This is currently only called for BLE beacon messages.
             *
             * For example, this is called when we see the first BLE advertisement
             * frame associated with a message; or when we see subsequent frames with
             * significantly different received signal strength indicator (RSSI)
             * readings.
             *
             * For more information, see the MessageListener Javadocs.
             */
            @Override
            public void onBleSignalChanged(final Message message, final BleSignal bleSignal) { }

            /**
             * Called when Nearby's estimate of the distance to a message changes.
             *
             * This is currently only called for BLE beacon messages.
             *
             * For more information, see the MessageListener Javadocs.
             */
            @Override
            public void onDistanceChanged(final Message message, final Distance distance) {
                if (Message.MESSAGE_NAMESPACE_RESERVED.equals(message.getNamespace())
                        && Message.MESSAGE_TYPE_EDDYSTONE_UID.equals(message.getType())) {
                    // Nearby provides the EddystoneUid class to parse Eddystone UIDs
                    // that have been found nearby.
                    EddystoneUid eddystoneUid = EddystoneUid.from(message);
                    long instance = Long.parseLong(eddystoneUid.getInstance(), 16);

                    Log.i(TAG, "Eddystone-UID " + eddystoneUid.getInstance() + " has new distance: " + distance.getMeters());

                    Log.d(TAG, "Eddystone Instance: " + instance);

                    MuseumWork targetWork = Constants.beaconWorkMap.get((int)instance);
                    targetWork.setDistance(distance.getMeters());

                    emitBeaconUpdate();
                }
            }

            /**
             * Called when a message is no longer detectable nearby.
             */
            @Override
            public void onLost(final Message message) {

                if (Message.MESSAGE_NAMESPACE_RESERVED.equals(message.getNamespace())
                        && Message.MESSAGE_TYPE_EDDYSTONE_UID.equals(message.getType())) {

                    EddystoneUid eddystoneUid = EddystoneUid.from(message);
                    long instance = Long.parseLong(eddystoneUid.getInstance(), 16);

                    Log.i(TAG, "Eddystone Lost message: " + message);



                    MuseumWork targetWork = Constants.beaconWorkMap.get((int) instance);
                    targetWork.setDistance(0.0);

                    emitBeaconUpdate();
                }

            }
        };

        Nearby.getMessagesClient(this).subscribe(messageListener, options);


        // Set the Navigate Fragment as default
        Fragment fragment = new NavigateFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission granted, Loading Mission Control!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "App need FINE LOCATION ACCESS to discover nearby Wifi APs",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
        }
    }

    // Setting default values in case fo 1st run
    private void setDefaultPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isFirstRun = sharedPreferences.contains(Constants.IS_FIRST_RUN);

        if(isFirstRun == false) {
            String  uniqueID = "user" + UUID.randomUUID().toString();
            editor.putString(Constants.USER_NAME, uniqueID);
            editor.putString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
            editor.putString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
            editor.putInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
            editor.putInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
            editor.putInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
            editor.putBoolean(Constants.IS_FIRST_RUN, false);
            editor.apply();
        }
    }

    private void emitBeaconUpdate() {
        Intent intent = new Intent("beacon-update");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;
        if (id == R.id.nav_floorplan) {
            fragment = new NavigateFragment();
        } else {
            // Anything else is home
            fragment = new LearnFragment();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
