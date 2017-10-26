package com.find.wifitool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by akshay on 30/12/16.
 */

public class InternalDataBase extends SQLiteOpenHelper {

    // Private variables
    private static final String DATABASE_NAME = "WifiDB";
    private static final String TABLE_NAME = "Wifi_db";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "WIFINAME";
    private static final String COLUMN_GROUP = "WIFIGROUP";
    private static final String COLUMN_USER = "WIFIUSER";
    private static final int DATABASE_VERSION = 3;

    private static final String LT_TABLE_NAME = "locations";
    private static final String LT_COLUMN_ID = "ID";
    private static final String LT_COLUMN_LOC_NAME = "loc_name";
    private static final String LT_COLUMN_FLOOR_NAME = "floor_name";
    private static final String LT_COLUMN_X = "loc_x";
    private static final String LT_COLUMN_Y = "loc_y";
    private static final String LT_COLUMN_RATIO = "loc_ratio";
    private static final String LT_COLUMN_EXHIBIT = "loc_exhibit";



    public InternalDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " VARCHAR, "
                + COLUMN_GROUP + " VARCHAR, "
                + COLUMN_USER + " VARCHAR);"

                + "CREATE TABLE IF NOT EXISTS " + LT_TABLE_NAME
                + "(" + LT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LT_COLUMN_LOC_NAME + " VARCHAR, "
                + LT_COLUMN_FLOOR_NAME + " VARCHAR, "
                + LT_COLUMN_X + " REAL, "
                + LT_COLUMN_Y + " REAL, "
                + LT_COLUMN_RATIO + " REAL, "
                + LT_COLUMN_EXHIBIT + " INTEGER);";



        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + "; DROP TABLE IF EXISTS " + LT_TABLE_NAME;
        db.execSQL(sql);

        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new event
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getWifiName());
        values.put(COLUMN_GROUP, event.getWifiGroup());
        values.put(COLUMN_USER, event.getWifiUser());

        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting all records
    public List<Event> getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<Event> eventList = new ArrayList<Event>();

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(Integer.parseInt(cursor.getString(0)));
                event.setWifiName(cursor.getString(1));
                event.setWifiGroup(cursor.getString(2));
                event.setWifiUser(cursor.getString(3));
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }


    public void addLocation(FloorLocation loc) {
        SQLiteDatabase db = this.getWritableDatabase();

        FloorLocation savedLoc = getLocation(loc.getLocName());

        ContentValues values = new ContentValues();
        values.put(LT_COLUMN_LOC_NAME, loc.getLocName());
        values.put(LT_COLUMN_FLOOR_NAME, loc.getFloorName());
        values.put(LT_COLUMN_X, loc.getLocX());
        values.put(LT_COLUMN_Y, loc.getLocY());
        values.put(LT_COLUMN_RATIO, loc.getLocRatio());

        if(savedLoc == null) {
            db.insert(LT_TABLE_NAME, null, values);
        } else {
            db.update(LT_TABLE_NAME, values, LT_COLUMN_ID + "=?", new String[] {"" + savedLoc.getID()} );
        }
        db.close();
    }

    // Getting all records
    public FloorLocation getLocation(String locName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + LT_TABLE_NAME + " WHERE " + LT_COLUMN_LOC_NAME + "=? LIMIT 1", new String[] {locName});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                FloorLocation loc = new FloorLocation();
                loc.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(LT_COLUMN_ID))));
                loc.setLocName(cursor.getString(cursor.getColumnIndex(LT_COLUMN_LOC_NAME)));
                loc.setFloorName(cursor.getString(cursor.getColumnIndex(LT_COLUMN_FLOOR_NAME)));
                loc.setLocX(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LT_COLUMN_X))));
                loc.setLocY(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LT_COLUMN_Y))));
                loc.setLocRatio(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LT_COLUMN_RATIO))));

                return loc;
            }
            cursor.close();
        } catch(Exception e){
            Log.d(TAG, "Query failed");
        }
        return null;
    }

    // Deleting all records
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
